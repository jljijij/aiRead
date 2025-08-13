package com.shanzha.service;

import com.shanzha.domain.ChapterPackage;
import com.shanzha.repository.ChapterPackageRepository;
import com.shanzha.service.dto.ChapterPackageDTO;
import com.shanzha.service.mapper.ChapterPackageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.ChapterPackage}.
 */
@Service
@Transactional
public class ChapterPackageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterPackageService.class);

    private final ChapterPackageRepository chapterPackageRepository;

    private final ChapterPackageMapper chapterPackageMapper;

    public ChapterPackageService(ChapterPackageRepository chapterPackageRepository, ChapterPackageMapper chapterPackageMapper) {
        this.chapterPackageRepository = chapterPackageRepository;
        this.chapterPackageMapper = chapterPackageMapper;
    }

    /**
     * Save a chapterPackage.
     *
     * @param chapterPackageDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterPackageDTO save(ChapterPackageDTO chapterPackageDTO) {
        LOG.debug("Request to save ChapterPackage : {}", chapterPackageDTO);
        ChapterPackage chapterPackage = chapterPackageMapper.toEntity(chapterPackageDTO);
        chapterPackage = chapterPackageRepository.save(chapterPackage);
        return chapterPackageMapper.toDto(chapterPackage);
    }

    /**
     * Update a chapterPackage.
     *
     * @param chapterPackageDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterPackageDTO update(ChapterPackageDTO chapterPackageDTO) {
        LOG.debug("Request to update ChapterPackage : {}", chapterPackageDTO);
        ChapterPackage chapterPackage = chapterPackageMapper.toEntity(chapterPackageDTO);
        chapterPackage = chapterPackageRepository.save(chapterPackage);
        return chapterPackageMapper.toDto(chapterPackage);
    }

    /**
     * Partially update a chapterPackage.
     *
     * @param chapterPackageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChapterPackageDTO> partialUpdate(ChapterPackageDTO chapterPackageDTO) {
        LOG.debug("Request to partially update ChapterPackage : {}", chapterPackageDTO);

        return chapterPackageRepository
            .findById(chapterPackageDTO.getId())
            .map(existingChapterPackage -> {
                chapterPackageMapper.partialUpdate(existingChapterPackage, chapterPackageDTO);

                return existingChapterPackage;
            })
            .map(chapterPackageRepository::save)
            .map(chapterPackageMapper::toDto);
    }

    /**
     * Get all the chapterPackages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChapterPackageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChapterPackages");
        return chapterPackageRepository.findAll(pageable).map(chapterPackageMapper::toDto);
    }

    /**
     * Get one chapterPackage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChapterPackageDTO> findOne(Long id) {
        LOG.debug("Request to get ChapterPackage : {}", id);
        return chapterPackageRepository.findById(id).map(chapterPackageMapper::toDto);
    }

    /**
     * Delete the chapterPackage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ChapterPackage : {}", id);
        chapterPackageRepository.deleteById(id);
    }
}
