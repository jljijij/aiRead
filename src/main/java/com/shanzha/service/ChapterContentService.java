package com.shanzha.service;

import com.shanzha.domain.Chapter;
import com.shanzha.domain.ChapterContent;
import com.shanzha.repository.ChapterContentRepository;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
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
}
