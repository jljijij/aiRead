package com.shanzha.web.rest.controller;



import com.shanzha.domain.Chapter;
import com.shanzha.repository.ChapterRepository;
import com.shanzha.service.ChapterService;
import com.shanzha.service.ChunkUploadService;
import com.shanzha.service.dto.ChapterDTO;
import com.shanzha.service.dto.ChapterIdTitleDTO;
import com.shanzha.service.dto.CreateChapterParam;
import com.shanzha.utils.IdGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/chapters")
@Tag(name = "章节内容管理", description = "章节内容的上传、查询和管理")
public class ChapterPublishController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    ChunkUploadService chunkUploadService;

    @Operation(summary = "创建章节", description = "章节上传")
    @PostMapping("/create_chapter")
    public ResponseEntity<Long> createChapter(@RequestBody CreateChapterParam createChapterParam) {
        ChapterDTO chapter = new ChapterDTO();
        chapter.setId(createChapterParam.getNovelId());
        chapter.setChapterNo(createChapterParam.getChapterNum());
        chapter.setPrice(0);
        chapter.setTitle(createChapterParam.getChapterTitle());

        Long id = IdGenerator.nextId();
        chapter.setId(id);
        chapter.setIsVip(false);
        chapter.setPrice(0);
        chapter.setWordCount(0);
        chapter.setUpdateTime(new Date().toInstant());
        chapter.setCreateTime(new Date().toInstant());
        chapter.setContentId(1L);
        log.info("here");
        chapterService.save(chapter);

        return ResponseEntity.ok(id); // 200 OK + id 作为响应体
    }

    @PostMapping(value = "/chunk",headers = "content-type=multipart/form-data")
    @Operation(summary = "上传文件分片", description = "用于大文件的分片上传")
    public ResponseEntity<String> uploadChunk(
        @RequestPart("file") MultipartFile file,
        @RequestParam("novelId") Long novelId,
        @RequestParam("chapterId") Long chapterId,
        @RequestParam("chunkNum") Integer chunkNum,
        @RequestParam("totalChunks") Integer totalChunks,
        @RequestParam("identifier") String fileHash) {

        Boolean b = chunkUploadService.handleChunkUpload(file, novelId, chapterId, chunkNum, totalChunks, fileHash);
        return ResponseEntity.ok("上传成功");
    }


    @GetMapping("/by-novel/{novelId}")
    public ResponseEntity<List<ChapterIdTitleDTO>> getChapterIdAndTitle(@PathVariable Long novelId) {
        return ResponseEntity.ok(chapterService.getIdTitleList(novelId));
    }


}



