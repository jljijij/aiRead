package com.shanzha.service;

import com.shanzha.domain.Chapter;
import com.shanzha.repository.ChapterRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChapterService {

    private final ChapterRepository chapterRepository;

    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    @Cacheable(value = com.shanzha.domain.Chapter.class.getName(), key = "#id")
    public Chapter findOne(Long id) {
        return chapterRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = com.shanzha.domain.Chapter.class.getName(), key = "#result.id", condition = "#result != null")
    public Chapter save(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    @CacheEvict(value = com.shanzha.domain.Chapter.class.getName(), key = "#id")
    public void delete(Long id) {
        chapterRepository.deleteById(id);
    }

    public java.util.List<Chapter> findAll() {
        return chapterRepository.findAll();
    }
}
