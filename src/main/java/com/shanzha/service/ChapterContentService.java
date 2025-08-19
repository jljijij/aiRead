package com.shanzha.service;

import com.shanzha.domain.Chapter;
import com.shanzha.domain.ChapterContent;
import com.shanzha.repository.ChapterContentRepository;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing ChapterContent with a Bloom filter to track seen chapters.
 */
@Service
@Transactional
public class ChapterContentService {

    private final ChapterContentRepository chapterContentRepository;
    private final RBloomFilter<Long> bloomFilter;

    public ChapterContentService(ChapterContentRepository chapterContentRepository, RedissonClient redissonClient) {
        this.chapterContentRepository = chapterContentRepository;
        this.bloomFilter = redissonClient.getBloomFilter("chapterContent");
    }

    @PostConstruct
    public void initBloomFilter() {
        chapterContentRepository
            .findAll()
            .stream()
            .map(ChapterContent::getChapter)
            .filter(Objects::nonNull)
            .map(Chapter::getId)
            .filter(Objects::nonNull)
            .forEach(bloomFilter::add);
    }

    @Cacheable(value = com.shanzha.domain.ChapterContent.class.getName(), key = "#id")
    public ChapterContent findOne(Long id) {
        return chapterContentRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = com.shanzha.domain.ChapterContent.class.getName(), key = "#result.id", condition = "#result != null")
    public ChapterContent save(ChapterContent chapterContent) {
        if (chapterContent.getChapter() != null && chapterContent.getChapter().getId() != null) {
            bloomFilter.add(chapterContent.getChapter().getId());
        }
        return chapterContentRepository.save(chapterContent);
    }

    @CacheEvict(value = com.shanzha.domain.ChapterContent.class.getName(), key = "#id")
    public void delete(Long id) {
        chapterContentRepository.deleteById(id);
    }

    public java.util.List<ChapterContent> findAll() {
        return chapterContentRepository.findAll();
    }
}
