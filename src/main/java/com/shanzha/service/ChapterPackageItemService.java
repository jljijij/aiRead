package com.shanzha.service;

import com.shanzha.domain.ChapterPackageItem;
import com.shanzha.repository.ChapterPackageItemRepository;
import com.shanzha.service.dto.ChapterPackageItemDTO;
import com.shanzha.service.mapper.ChapterPackageItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.ChapterPackageItem}.
 */
@Service
@Transactional
public class ChapterPackageItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterPackageItemService.class);

    private final ChapterPackageItemRepository chapterPackageItemRepository;

    private final ChapterPackageItemMapper chapterPackageItemMapper;

    public ChapterPackageItemService(
        ChapterPackageItemRepository chapterPackageItemRepository,
        ChapterPackageItemMapper chapterPackageItemMapper
    ) {
        this.chapterPackageItemRepository = chapterPackageItemRepository;
        this.chapterPackageItemMapper = chapterPackageItemMapper;
    }

    /**
     * Save a chapterPackageItem.
     *
     * @param chapterPackageItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterPackageItemDTO save(ChapterPackageItemDTO chapterPackageItemDTO) {
        LOG.debug("Request to save ChapterPackageItem : {}", chapterPackageItemDTO);
        ChapterPackageItem chapterPackageItem = chapterPackageItemMapper.toEntity(chapterPackageItemDTO);
        chapterPackageItem = chapterPackageItemRepository.save(chapterPackageItem);
        return chapterPackageItemMapper.toDto(chapterPackageItem);
    }

    /**
     * Update a chapterPackageItem.
     *
     * @param chapterPackageItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterPackageItemDTO update(ChapterPackageItemDTO chapterPackageItemDTO) {
        LOG.debug("Request to update ChapterPackageItem : {}", chapterPackageItemDTO);
        ChapterPackageItem chapterPackageItem = chapterPackageItemMapper.toEntity(chapterPackageItemDTO);
        chapterPackageItem = chapterPackageItemRepository.save(chapterPackageItem);
        return chapterPackageItemMapper.toDto(chapterPackageItem);
    }

    /**
     * Partially update a chapterPackageItem.
     *
     * @param chapterPackageItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChapterPackageItemDTO> partialUpdate(ChapterPackageItemDTO chapterPackageItemDTO) {
        LOG.debug("Request to partially update ChapterPackageItem : {}", chapterPackageItemDTO);

        return chapterPackageItemRepository
            .findById(chapterPackageItemDTO.getId())
            .map(existingChapterPackageItem -> {
                chapterPackageItemMapper.partialUpdate(existingChapterPackageItem, chapterPackageItemDTO);

                return existingChapterPackageItem;
            })
            .map(chapterPackageItemRepository::save)
            .map(chapterPackageItemMapper::toDto);
    }

    /**
     * Get all the chapterPackageItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChapterPackageItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChapterPackageItems");
        return chapterPackageItemRepository.findAll(pageable).map(chapterPackageItemMapper::toDto);
    }

    /**
     * Get one chapterPackageItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChapterPackageItemDTO> findOne(Long id) {
        LOG.debug("Request to get ChapterPackageItem : {}", id);
        return chapterPackageItemRepository.findById(id).map(chapterPackageItemMapper::toDto);
    }

    /**
     * Delete the chapterPackageItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ChapterPackageItem : {}", id);
        chapterPackageItemRepository.deleteById(id);
    }
}
