package com.shanzha.web.rest;

import com.shanzha.domain.ChapterContent;
import com.shanzha.service.ChapterContentService;
import com.shanzha.service.RateLimitService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chapter-contents")
public class ChapterContentResource {

    private final ChapterContentService chapterContentService;
    private final RateLimitService rateLimitService;

    public ChapterContentResource(ChapterContentService chapterContentService, RateLimitService rateLimitService) {
        this.chapterContentService = chapterContentService;
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterContent> get(@PathVariable Long id) {
        if (!rateLimitService.tryConsume()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(chapterContentService.findOne(id));
    }

    @GetMapping
    public ResponseEntity<List<ChapterContent>> all() {
        if (!rateLimitService.tryConsume()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(chapterContentService.findAll());
    }

    @PostMapping
    public ResponseEntity<ChapterContent> create(@RequestBody ChapterContent cc) {
        return new ResponseEntity<>(chapterContentService.save(cc), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChapterContent> update(@PathVariable Long id, @RequestBody ChapterContent cc) {
        cc.setId(id);
        return ResponseEntity.ok(chapterContentService.save(cc));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chapterContentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
