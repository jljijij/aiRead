package com.shanzha.service;

import com.shanzha.domain.ChapterUpdate;
import com.shanzha.domain.CommonCodeMsg;
import com.shanzha.service.dto.ChapterContentDTO;
import com.shanzha.utils.IdGenerator;
import com.shanzha.web.rest.errors.BusinessException;
import jakarta.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ChunkUploadService {

    @Value("${upload.temp-dir:/tmp/chunks}")
    private String tempDir;

    @Value("${upload.target-dir:/tmp/merged}")
    private String targetDir;

    // 记录每个 fileHash 的最后上传时间
    private final ConcurrentHashMap<String, Long> chunkUploadTracker = new ConcurrentHashMap<>();

    // 超时毫秒（5分钟）
    private static final long TIMEOUT_MILLIS = 5 * 60 * 1000;

    @Resource
    private ChapterContentService chapterContentService;

    @Resource
    private MessageSendingOperations<String> rocketMQTemplate;

    public Boolean handleChunkUpload(MultipartFile file, Long novelId, Long chapterId, Integer chunkNum, int totalChunks, String fileHash) {
        try {
            if (file.isEmpty()) {
                throw new BusinessException(CommonCodeMsg.IS_EMPTY);
            }

            Path chunkDir = Paths.get(tempDir, fileHash);
            Files.createDirectories(chunkDir);
            Path chunkPath = chunkDir.resolve(String.valueOf(chunkNum));
            file.transferTo(chunkPath);

            // 更新时间戳
            chunkUploadTracker.put(fileHash, System.currentTimeMillis());

            long uploadedChunks = Files.list(chunkDir).count();
            boolean isComplete = uploadedChunks == totalChunks;
            if (isComplete) {
                mergeChunks(fileHash, totalChunks, novelId, chapterId);
                chunkUploadTracker.remove(fileHash); // 上传完删除记录
            }
            return true;
        } catch (IOException e) {
            log.error("上传分片异常", e);
            return false;
        }
    }

    private void mergeChunks(String fileHash, int totalChunks, Long novelId, Long chapterId) throws IOException {
        Path chunkDir = Paths.get(tempDir, fileHash);
        Path destFile = Paths.get(targetDir, String.valueOf(novelId), "chapter-" + chapterId);
        Files.createDirectories(destFile.getParent());

        try (OutputStream os = Files.newOutputStream(destFile, StandardOpenOption.CREATE)) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunk = chunkDir.resolve(String.valueOf(i + 1));
                Files.copy(chunk, os);
                Files.delete(chunk);
            }
        }

        processCompletedFile(destFile, novelId, chapterId);
        FileUtils.deleteDirectory(chunkDir.toFile());
    }

    private void processCompletedFile(Path desFile, Long novelId, Long chapterId) throws IOException {
        try (FileInputStream fis = new FileInputStream(desFile.toFile()); XWPFDocument doc = new XWPFDocument(fis)) {
            int currentNum = 0;
            StringBuilder sb = new StringBuilder();

            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText();
                sb.append(text);
                if (sb.length() >= 1024 * 1024) {
                    currentNum = savePage(novelId, chapterId, currentNum, sb);
                }
            }

            if (sb.length() > 0) {
                savePage(novelId, chapterId, currentNum, sb);
            }
        }

        ChapterUpdate update = new ChapterUpdate();
        update.setChapterId(chapterId);
        update.setNovelId(novelId);

        rocketMQTemplate.send("chapter_update", MessageBuilder.withPayload(update).build());
    }

    private int savePage(Long novelId, Long chapterId, int currentNum, StringBuilder text) {
        ChapterContentDTO chapterContents = new ChapterContentDTO();
        chapterContents.setNovelId(novelId);
        chapterContents.setCreateTime(new Date().toInstant());
        chapterContents.setId(chapterId);
        chapterContents.setCompressed(text.toString().getBytes(StandardCharsets.UTF_8));
        chapterContents.setId(IdGenerator.nextId());
        chapterContents.setPageId((long) currentNum);

        chapterContentService.save(chapterContents);
        text.setLength(0); // 清空
        return currentNum + 1;
    }

    @Scheduled(fixedRate = 60_000) // 每分钟执行一次
    public void cleanupExpiredChunks() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : chunkUploadTracker.entrySet()) {
            String fileHash = entry.getKey();
            Long lastUploadTime = entry.getValue();
            if (now - lastUploadTime > TIMEOUT_MILLIS) {
                Path chunkDir = Paths.get(tempDir, fileHash);
                try {
                    FileUtils.deleteDirectory(chunkDir.toFile());
                    chunkUploadTracker.remove(fileHash);
                    log.info("已清理超时未完成上传的分片目录：{}", chunkDir);
                } catch (IOException e) {
                    log.error("清理分片目录失败：{}", chunkDir, e);
                }
            }
        }
    }
}
