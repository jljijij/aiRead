package com.shanzha.service;

import com.shanzha.domain.ChapterContent;
import com.shanzha.domain.CommonCodeMsg;
import com.shanzha.repository.ChapterContentRepository;
import com.shanzha.service.dto.ChapterContentDTO;
import com.shanzha.service.dto.RedisContent;
import com.shanzha.service.mapper.ChapterContentMapper;
import com.shanzha.web.rest.errors.BusinessException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.ChapterContent}.
 */
@Service
@Transactional
@Slf4j
public class ChapterContentService {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterContentService.class);

    private final ChapterContentRepository chapterContentRepository;
    private static final String CACHE_KEY_PREFIX = "novel:";
    private static final String LOCK_PREFIX = "lock:novel:";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final ChapterContentMapper chapterContentMapper;
    static final String BLOOM_FILTER_NAME = "bloom:chapterContent";
    private static final int EXPECTED_INSERTIONS = 1_000_000;
    private static final double FALSE_PROBABILITY = 0.01;

    public ChapterContentService(ChapterContentRepository chapterContentRepository, ChapterContentMapper chapterContentMapper) {
        this.chapterContentRepository = chapterContentRepository;
        this.chapterContentMapper = chapterContentMapper;
    }

    private String buildChapterKey(Long novelId, Long chapterId, Long pageId) {
        return novelId + "::" + chapterId + "::" + pageId;
    }

    private String buildCacheKey(Long novelId, Long chapterId, Long pageId) {
        return CACHE_KEY_PREFIX + novelId + "--" + chapterId + "--" + pageId;
    }

    private void invalidateCache(Long novelId, Long chapterId, Long pageId) {
        String cacheKey = buildCacheKey(novelId, chapterId, pageId);
        redissonClient.getBucket(cacheKey).delete();
        stringRedisTemplate.delete(cacheKey);
    }

    private void addBloomFilterKey(Long novelId, Long chapterId, Long pageId) {
        String bloomKey = buildChapterKey(novelId, chapterId, pageId);
        redissonClient.getBloomFilter(BLOOM_FILTER_NAME).add(bloomKey);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void initBloomFilter() {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BLOOM_FILTER_NAME);
        // tryInit 本身就是幂等的。返回 true 表示“刚初始化”，false 表示“已存在”
        boolean firstInit = bloomFilter.tryInit(EXPECTED_INSERTIONS, FALSE_PROBABILITY);
        if (!firstInit) {
            return; // 已初始化过，避免重复全量加载
        }

        List<ChapterContentRepository.ChapterKeyView> keys = chapterContentRepository.findAllChapterKeys();
        for (ChapterContentRepository.ChapterKeyView k : keys) {
            Long novelId = k.getNovelId();
            Long chapterId = k.getChapterId();
            Long pageId = k.getPageId();
            if (novelId == null || chapterId == null || pageId == null) {
                continue; // 兜底
            }
            String key = buildChapterKey(novelId, chapterId, pageId);
            bloomFilter.add(key);
        }
    }

    /**
     * Save a chapterContent.
     *
     * @param chapterContentDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterContentDTO save(ChapterContentDTO chapterContentDTO) {
        LOG.debug("Request to save ChapterContent : {}", chapterContentDTO);
        ChapterContent chapterContent = chapterContentMapper.toEntity(chapterContentDTO);
        chapterContent = chapterContentRepository.save(chapterContent);
        Long novelId = chapterContent.getNovelId();
        Long chapterId = chapterContent.getChapter() != null ? chapterContent.getChapter().getId() : null;
        Long pageId = chapterContent.getPageId();
        if (novelId != null && chapterId != null && pageId != null) {
            redissonClient.getBloomFilter(BLOOM_FILTER_NAME).add(buildChapterKey(novelId, chapterId, pageId));
        }
        return chapterContentMapper.toDto(chapterContent);
    }

    /**
     * Update a chapterContent.
     *
     * @param chapterContentDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterContentDTO update(ChapterContentDTO chapterContentDTO) {
        LOG.debug("Request to update ChapterContent : {}", chapterContentDTO);
        ChapterContent chapterContent = chapterContentMapper.toEntity(chapterContentDTO);
        chapterContent = chapterContentRepository.save(chapterContent);
        Long novelId = chapterContent.getNovelId();
        Long chapterId = chapterContent.getChapter().getId();
        Long pageId = chapterContent.getPageId();
        if (novelId != null && chapterId != null && pageId != null) {
            invalidateCache(novelId, chapterId, pageId);
            addBloomFilterKey(novelId, chapterId, pageId);
        }
        return chapterContentMapper.toDto(chapterContent);
    }

    /**
     * Partially update a chapterContent.
     *
     * @param chapterContentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChapterContentDTO> partialUpdate(ChapterContentDTO chapterContentDTO) {
        LOG.debug("Request to partially update ChapterContent : {}", chapterContentDTO);

        return chapterContentRepository
            .findById(chapterContentDTO.getId())
            .map(existingChapterContent -> {
                chapterContentMapper.partialUpdate(existingChapterContent, chapterContentDTO);

                return existingChapterContent;
            })
            .map(chapterContentRepository::save)
            .map(saved -> {
                Long novelId = saved.getNovelId();
                Long chapterId = saved.getChapter().getId();
                Long pageId = saved.getPageId();
                if (novelId != null && chapterId != null && pageId != null) {
                    invalidateCache(novelId, chapterId, pageId);
                    addBloomFilterKey(novelId, chapterId, pageId);
                }
                return chapterContentMapper.toDto(saved);
            });
    }

    /**
     * Get all the chapterContents.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ChapterContentDTO> findAll() {
        LOG.debug("Request to get all ChapterContents");
        return chapterContentRepository
            .findAll()
            .stream()
            .map(chapterContentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the chapterContents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ChapterContentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return chapterContentRepository.findAllWithEagerRelationships(pageable).map(chapterContentMapper::toDto);
    }

    /**
     * Get one chapterContent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChapterContentDTO> findOne(Long id) {
        LOG.debug("Request to get ChapterContent : {}", id);
        return chapterContentRepository.findOneWithEagerRelationships(id).map(chapterContentMapper::toDto);
    }

    /**
     * Delete the chapterContent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ChapterContent : {}", id);
        chapterContentRepository
            .findById(id)
            .ifPresent(content -> invalidateCache(content.getNovelId(), content.getChapter().getId(), content.getPageId()));
        chapterContentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ChapterContentDTO> getContent(Long novelId, Long chapterId, Long pageId) {
        log.debug("Request to get ChapterContent by novelId={}, chapterId={}, pageId={}", novelId, chapterId, pageId);
        return chapterContentRepository.findByNovelIdAndChapterIdAndPageId(novelId, chapterId, pageId).map(chapterContentMapper::toDto);
    }

    public String readChapterContent(Long novelId, Long chapterId, Long pageId) throws InterruptedException {
        String cacheKey = CACHE_KEY_PREFIX + novelId + "--" + chapterId + "--" + pageId;
        String lockKey = LOCK_PREFIX + novelId + "--" + chapterId + "--" + pageId;
        String bloomKey = buildChapterKey(novelId, chapterId, pageId);

        // 1. 先通过布隆过滤器判断是否可能存在
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BLOOM_FILTER_NAME);
        if (!bloomFilter.contains(bloomKey)) {
            throw new BusinessException(CommonCodeMsg.NOT_FIND); // 提前返回，不进缓存和数据库
        }
        RBucket<String> bucket = redissonClient.getBucket(cacheKey);
        String cached = bucket.get();

        if (cached != null) {
            return cached;
        }

        RLock lock = redissonClient.getLock(lockKey);

        // 最多等 3 秒获取锁，10 秒后自动释放
        if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
            try {
                // 双检锁
                cached = bucket.get();
                if (cached != null) {
                    return cached;
                }

                Optional<ChapterContentDTO> contentOptional = getContent(novelId, chapterId, pageId);
                if (contentOptional.isEmpty()) {
                    bucket.set("小说章节消失了", 5, TimeUnit.MINUTES); // 缓存空值防止穿透
                    throw new BusinessException(CommonCodeMsg.NOT_FIND);
                }

                ChapterContentDTO content = contentOptional.get();
                byte[] compressed = content.getCompressed();

                return isGone(bucket, compressed);
            } finally {
                lock.unlock();
            }
        } else {
            throw new BusinessException(CommonCodeMsg.TOO_MANY_REQUESTS);
        }
    }

    public static String isGone(RBucket<String> bucket, byte[] compressed) {
        if (compressed == null || compressed.length == 0) {
            bucket.set("小说章节消失了", 5, TimeUnit.MINUTES);
            throw new BusinessException(CommonCodeMsg.NOT_FIND);
        }

        String result = new String(compressed, StandardCharsets.UTF_8);
        bucket.set(result, 1, TimeUnit.HOURS); // 缓存内容
        return result;
    }

    public Page<RedisContent> getRedisContentsByNovelId(Long novelId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("chapter.id").ascending().and(Sort.by("pageId").ascending()));
        return chapterContentRepository.findByNovelId(novelId, pageable);
    }

    public void deleteChapterContents(Long novelId, Long chapterId) {
        // 删除数据库中章节分页内容
        List<ChapterContent> contents = chapterContentRepository.findByNovelIdAndChapterId(novelId, chapterId);
        chapterContentRepository.deleteAll(contents);
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BLOOM_FILTER_NAME);
        // 清理缓存
        for (ChapterContent content : contents) {
            Long pageId = content.getPageId();
            invalidateCache(novelId, chapterId, pageId);
        }
    }
}
