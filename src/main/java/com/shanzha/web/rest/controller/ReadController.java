package com.shanzha.web.rest.controller;

import com.shanzha.domain.CommonCodeMsg;
import com.shanzha.service.ChapterContentService;
import com.shanzha.service.dto.ChapterContentDTO;
import com.shanzha.web.rest.errors.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/read")
@Tag(name = "阅读相关接口", description = "阅读相关接口")
@Slf4j
public class ReadController {

    @Autowired
    private ChapterContentService chapterContentService;

    @GetMapping("/{novel_id}/{chapter_id}/{page_id}")
    @Operation(summary = "阅读", description = "阅读")
    public ResponseEntity<String> read(
        @PathVariable Long novel_id,
        @PathVariable Long chapter_id,
        @PathVariable Long page_id
    ) throws InterruptedException {
        String result = chapterContentService.readChapterContent(novel_id, chapter_id, page_id);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/hotest_novel")
    public ResponseEntity<List<Long>> getHot() {
        return ResponseEntity.ok(null); // 待实现热门小说逻辑
    }
}
