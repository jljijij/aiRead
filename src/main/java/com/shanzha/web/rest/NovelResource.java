package com.shanzha.web.rest;

import com.shanzha.service.NovelService;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/novels")
public class NovelResource {

    private final NovelService novelService;

    public NovelResource(NovelService novelService) {
        this.novelService = novelService;
    }

    @PostMapping("/parse")
    public ResponseEntity<String> parse(@RequestParam("file") MultipartFile file) throws IOException {
        String content = novelService.parseFile(file);
        return ResponseEntity.ok(content);
    }
}
