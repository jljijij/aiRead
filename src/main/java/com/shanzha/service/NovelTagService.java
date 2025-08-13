package com.shanzha.service;

import com.shanzha.domain.NovelTag;
import com.shanzha.repository.NovelTagRepository;
import com.shanzha.service.dto.NovelTagDTO;
import com.shanzha.service.mapper.NovelTagMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.NovelTag}.
 */
@Service
@Transactional
public class NovelTagService {

    private static final Logger LOG = LoggerFactory.getLogger(NovelTagService.class);

    private final NovelTagRepository novelTagRepository;

    private final NovelTagMapper novelTagMapper;

    public NovelTagService(NovelTagRepository novelTagRepository, NovelTagMapper novelTagMapper) {
        this.novelTagRepository = novelTagRepository;
        this.novelTagMapper = novelTagMapper;
    }

    /**
     * Save a novelTag.
     *
     * @param novelTagDTO the entity to save.
     * @return the persisted entity.
     */
    public NovelTagDTO save(NovelTagDTO novelTagDTO) {
        LOG.debug("Request to save NovelTag : {}", novelTagDTO);
        NovelTag novelTag = novelTagMapper.toEntity(novelTagDTO);
        novelTag = novelTagRepository.save(novelTag);
        return novelTagMapper.toDto(novelTag);
    }

    /**
     * Update a novelTag.
     *
     * @param novelTagDTO the entity to save.
     * @return the persisted entity.
     */
    public NovelTagDTO update(NovelTagDTO novelTagDTO) {
        LOG.debug("Request to update NovelTag : {}", novelTagDTO);
        NovelTag novelTag = novelTagMapper.toEntity(novelTagDTO);
        novelTag = novelTagRepository.save(novelTag);
        return novelTagMapper.toDto(novelTag);
    }

    /**
     * Partially update a novelTag.
     *
     * @param novelTagDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NovelTagDTO> partialUpdate(NovelTagDTO novelTagDTO) {
        LOG.debug("Request to partially update NovelTag : {}", novelTagDTO);

        return novelTagRepository
            .findById(novelTagDTO.getId())
            .map(existingNovelTag -> {
                novelTagMapper.partialUpdate(existingNovelTag, novelTagDTO);

                return existingNovelTag;
            })
            .map(novelTagRepository::save)
            .map(novelTagMapper::toDto);
    }

    /**
     * Get all the novelTags.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NovelTagDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NovelTags");
        return novelTagRepository.findAll(pageable).map(novelTagMapper::toDto);
    }

    /**
     * Get one novelTag by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NovelTagDTO> findOne(Long id) {
        LOG.debug("Request to get NovelTag : {}", id);
        return novelTagRepository.findById(id).map(novelTagMapper::toDto);
    }

    /**
     * Delete the novelTag by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete NovelTag : {}", id);
        novelTagRepository.deleteById(id);
    }
}
