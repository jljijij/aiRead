package com.shanzha.web.rest;

import com.shanzha.domain.Chapter;
import com.shanzha.service.ChapterService;
import com.shanzha.service.RateLimitService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chapters")
public class ChapterResource {

    private final ChapterService chapterService;
    private final RateLimitService rateLimitService;

    public ChapterResource(ChapterService chapterService, RateLimitService rateLimitService) {
        this.chapterService = chapterService;
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chapter> get(@PathVariable Long id) {
        if (!rateLimitService.tryConsume()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(chapterService.findOne(id));
    }

    @GetMapping
    public ResponseEntity<List<Chapter>> all() {
        if (!rateLimitService.tryConsume()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(chapterService.findAll());
    }

    @PostMapping
    public ResponseEntity<Chapter> create(@RequestBody Chapter chapter) {
        return new ResponseEntity<>(chapterService.save(chapter), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> update(@PathVariable Long id, @RequestBody Chapter chapter) {
        chapter.setId(id);
        return ResponseEntity.ok(chapterService.save(chapter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chapterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
