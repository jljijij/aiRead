package com.shanzha.service;

import com.shanzha.domain.Novel;
import com.shanzha.repository.NovelRepository;
import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class NovelService {

    private final NovelRepository novelRepository;

    public NovelService(NovelRepository novelRepository) {
        this.novelRepository = novelRepository;
    }

    public String parseFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File name is missing");
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else if (lower.endsWith(".docx")) {
            try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
                return document.getParagraphs().stream().map(XWPFParagraph::getText).collect(Collectors.joining("\n"));
            }
        } else if (lower.endsWith(".doc")) {
            try (HWPFDocument document = new HWPFDocument(file.getInputStream()); WordExtractor extractor = new WordExtractor(document)) {
                return extractor.getText();
            }
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    @Cacheable(value = com.shanzha.domain.Novel.class.getName(), key = "#id")
    public Novel findOne(Long id) {
        return novelRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = com.shanzha.domain.Novel.class.getName(), key = "#result.id", condition = "#result != null")
    public Novel save(Novel novel) {
        return novelRepository.save(novel);
    }

    @CacheEvict(value = com.shanzha.domain.Novel.class.getName(), key = "#id")
    public void delete(Long id) {
        novelRepository.deleteById(id);
    }

    public java.util.List<Novel> findAll() {
        return novelRepository.findAll();
    }
}
