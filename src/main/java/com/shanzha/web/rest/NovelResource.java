package com.shanzha.web.rest;

import com.shanzha.domain.Novel;
import com.shanzha.service.NovelService;
import com.shanzha.service.RateLimitService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/novels")
public class NovelResource {

    private final NovelService novelService;
    private final RateLimitService rateLimitService;

    public NovelResource(NovelService novelService, RateLimitService rateLimitService) {
        this.novelService = novelService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/parse")
    public ResponseEntity<String> parse(@RequestParam("file") MultipartFile file) throws IOException {
        String content = novelService.parseFile(file);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Novel> get(@PathVariable Long id) {
        if (!rateLimitService.tryConsume()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(novelService.findOne(id));
    }

    @GetMapping
    public ResponseEntity<List<Novel>> all() {
        if (!rateLimitService.tryConsume()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(novelService.findAll());
    }

    @PostMapping
    public ResponseEntity<Novel> create(@RequestBody Novel novel) {
        return new ResponseEntity<>(novelService.save(novel), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Novel> update(@PathVariable Long id, @RequestBody Novel novel) {
        novel.setId(id);
        return ResponseEntity.ok(novelService.save(novel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        novelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
