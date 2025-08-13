package com.shanzha.service;

import com.shanzha.domain.ChapterPermission;
import com.shanzha.repository.ChapterPermissionRepository;
import com.shanzha.service.dto.ChapterPermissionDTO;
import com.shanzha.service.mapper.ChapterPermissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.ChapterPermission}.
 */
@Service
@Transactional
public class ChapterPermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterPermissionService.class);

    private final ChapterPermissionRepository chapterPermissionRepository;

    private final ChapterPermissionMapper chapterPermissionMapper;

    public ChapterPermissionService(
        ChapterPermissionRepository chapterPermissionRepository,
        ChapterPermissionMapper chapterPermissionMapper
    ) {
        this.chapterPermissionRepository = chapterPermissionRepository;
        this.chapterPermissionMapper = chapterPermissionMapper;
    }

    /**
     * Save a chapterPermission.
     *
     * @param chapterPermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterPermissionDTO save(ChapterPermissionDTO chapterPermissionDTO) {
        LOG.debug("Request to save ChapterPermission : {}", chapterPermissionDTO);
        ChapterPermission chapterPermission = chapterPermissionMapper.toEntity(chapterPermissionDTO);
        chapterPermission = chapterPermissionRepository.save(chapterPermission);
        return chapterPermissionMapper.toDto(chapterPermission);
    }

    /**
     * Update a chapterPermission.
     *
     * @param chapterPermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public ChapterPermissionDTO update(ChapterPermissionDTO chapterPermissionDTO) {
        LOG.debug("Request to update ChapterPermission : {}", chapterPermissionDTO);
        ChapterPermission chapterPermission = chapterPermissionMapper.toEntity(chapterPermissionDTO);
        chapterPermission = chapterPermissionRepository.save(chapterPermission);
        return chapterPermissionMapper.toDto(chapterPermission);
    }

    /**
     * Partially update a chapterPermission.
     *
     * @param chapterPermissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChapterPermissionDTO> partialUpdate(ChapterPermissionDTO chapterPermissionDTO) {
        LOG.debug("Request to partially update ChapterPermission : {}", chapterPermissionDTO);

        return chapterPermissionRepository
            .findById(chapterPermissionDTO.getId())
            .map(existingChapterPermission -> {
                chapterPermissionMapper.partialUpdate(existingChapterPermission, chapterPermissionDTO);

                return existingChapterPermission;
            })
            .map(chapterPermissionRepository::save)
            .map(chapterPermissionMapper::toDto);
    }

    /**
     * Get all the chapterPermissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChapterPermissionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChapterPermissions");
        return chapterPermissionRepository.findAll(pageable).map(chapterPermissionMapper::toDto);
    }

    /**
     * Get one chapterPermission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChapterPermissionDTO> findOne(Long id) {
        LOG.debug("Request to get ChapterPermission : {}", id);
        return chapterPermissionRepository.findById(id).map(chapterPermissionMapper::toDto);
    }

    /**
     * Delete the chapterPermission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ChapterPermission : {}", id);
        chapterPermissionRepository.deleteById(id);
    }
}
