package com.shanzha.service;



import com.shanzha.domain.CommonCodeMsg;
import com.shanzha.service.dto.ChapterContentDTO;
import com.shanzha.web.rest.errors.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class ChapterContentReadService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ChapterContentService chapterContentService;

    private final Lock lock = new ReentrantLock();

    public String getChapterContentWithCache(Long novelId, Long chapterId, Long pageId) {
        String cacheKey = buildCacheKey(novelId, chapterId, pageId);

        // Step 1: 直接查缓存
        log.warn("尝试读取缓存: {}", cacheKey);
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.warn("命中缓存: {}", cached);
            return cached;
        }

        byte[] compressed;
        Optional<ChapterContentDTO> contentOption;

        // Step 2: 使用本地锁防止缓存穿透
        lock.lock();
        try {
            // 双重检查锁内缓存
            String doubleCheck = stringRedisTemplate.opsForValue().get(cacheKey);
            if (doubleCheck != null) {
                log.warn("锁内二次命中缓存: {}", doubleCheck);
                return doubleCheck;
            }

            log.warn("缓存未命中，查询数据库...");
            contentOption = chapterContentService.getContent(novelId, chapterId, pageId);
            ChapterContentDTO content = contentOption.get();
            if (content == null || (compressed = content.getCompressed()) == null || compressed.length == 0) {
                stringRedisTemplate.opsForValue().set(cacheKey, "小说章节消失了");
                throw new BusinessException(CommonCodeMsg.NOT_FIND);
            }

            String contentStr = new String(compressed, StandardCharsets.UTF_8);
            stringRedisTemplate.opsForValue().set(cacheKey, contentStr);
            return contentStr;

        } finally {
            lock.unlock();
        }
    }
    private String buildCacheKey(Long novelId, Long chapterId, Long pageId) {
        return String.format("novel:%d--%d--%d", novelId, chapterId, pageId);
    }
}
