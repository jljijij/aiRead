package com.shanzha.service;

import com.shanzha.domain.Novel;
import com.shanzha.repository.NovelRepository;
import com.shanzha.service.dto.NovelDTO;
import com.shanzha.service.mapper.NovelMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.shanzha.domain.Novel}.
 */
@Service
@Transactional
public class NovelService {

    private static final Logger LOG = LoggerFactory.getLogger(NovelService.class);
    private static final String RANK_KEY = "novel:read:rank";;

    private final NovelRepository novelRepository;

    private final NovelMapper novelMapper;


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
    public NovelService(NovelRepository novelRepository, NovelMapper novelMapper) {
        this.novelRepository = novelRepository;
        this.novelMapper = novelMapper;

    }

    /**
     * Save a novel.
     *
     * @param novelDTO the entity to save.
     * @return the persisted entity.
     */
    public NovelDTO save(NovelDTO novelDTO) {
        LOG.debug("Request to save Novel : {}", novelDTO);
        Novel novel = novelMapper.toEntity(novelDTO);
        novel = novelRepository.save(novel);
        return novelMapper.toDto(novel);
    }

    /**
     * Update a novel.
     *
     * @param novelDTO the entity to save.
     * @return the persisted entity.
     */
    public NovelDTO update(NovelDTO novelDTO) {
        LOG.debug("Request to update Novel : {}", novelDTO);
        Novel novel = novelMapper.toEntity(novelDTO);
        novel = novelRepository.save(novel);
        return novelMapper.toDto(novel);
    }

    /**
     * Partially update a novel.
     *
     * @param novelDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NovelDTO> partialUpdate(NovelDTO novelDTO) {
        LOG.debug("Request to partially update Novel : {}", novelDTO);

        return novelRepository
            .findById(novelDTO.getId())
            .map(existingNovel -> {
                novelMapper.partialUpdate(existingNovel, novelDTO);

                return existingNovel;
            })
            .map(novelRepository::save)
            .map(novelMapper::toDto);
    }

    /**
     * Get all the novels.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<NovelDTO> findAll() {
        LOG.debug("Request to get all Novels");
        return novelRepository.findAll().stream().map(novelMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one novel by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NovelDTO> findOne(Long id) {
        LOG.debug("Request to get Novel : {}", id);
        return novelRepository.findById(id).map(novelMapper::toDto);
    }

    /**
     * Delete the novel by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Novel : {}", id);
        novelRepository.deleteById(id);
    }

    public List<Long> getTop10HottestNovelIds() {
        Map<String, Double> rankMap = RedisClient.getTopZSetWithScore(RANK_KEY, 10);
        return rankMap.keySet().stream()
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }
}
