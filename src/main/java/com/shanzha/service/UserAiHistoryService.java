package com.shanzha.service;

import com.shanzha.domain.UserAiHistory;
import com.shanzha.repository.UserAiHistoryRepository;
import com.shanzha.service.dto.ChatItemVo;
import com.shanzha.service.dto.UserAiHistoryDTO;
import com.shanzha.service.mapper.UserAiHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.UserAiHistory}.
 */
@Service
@Transactional
public class UserAiHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(UserAiHistoryService.class);

    private final UserAiHistoryRepository userAiHistoryRepository;

    private final UserAiHistoryMapper userAiHistoryMapper;

    public UserAiHistoryService(UserAiHistoryRepository userAiHistoryRepository, UserAiHistoryMapper userAiHistoryMapper) {
        this.userAiHistoryRepository = userAiHistoryRepository;
        this.userAiHistoryMapper = userAiHistoryMapper;
    }

    /**
     * Save a userAiHistory.
     *
     * @param userAiHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public UserAiHistoryDTO save(UserAiHistoryDTO userAiHistoryDTO) {
        LOG.debug("Request to save UserAiHistory : {}", userAiHistoryDTO);
        UserAiHistory userAiHistory = userAiHistoryMapper.toEntity(userAiHistoryDTO);
        userAiHistory = userAiHistoryRepository.save(userAiHistory);
        return userAiHistoryMapper.toDto(userAiHistory);
    }

    /**
     * Update a userAiHistory.
     *
     * @param userAiHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public UserAiHistoryDTO update(UserAiHistoryDTO userAiHistoryDTO) {
        LOG.debug("Request to update UserAiHistory : {}", userAiHistoryDTO);
        UserAiHistory userAiHistory = userAiHistoryMapper.toEntity(userAiHistoryDTO);
        userAiHistory = userAiHistoryRepository.save(userAiHistory);
        return userAiHistoryMapper.toDto(userAiHistory);
    }

    /**
     * Partially update a userAiHistory.
     *
     * @param userAiHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserAiHistoryDTO> partialUpdate(UserAiHistoryDTO userAiHistoryDTO) {
        LOG.debug("Request to partially update UserAiHistory : {}", userAiHistoryDTO);

        return userAiHistoryRepository
            .findById(userAiHistoryDTO.getId())
            .map(existingUserAiHistory -> {
                userAiHistoryMapper.partialUpdate(existingUserAiHistory, userAiHistoryDTO);

                return existingUserAiHistory;
            })
            .map(userAiHistoryRepository::save)
            .map(userAiHistoryMapper::toDto);
    }

    /**
     * Get all the userAiHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserAiHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserAiHistories");
        return userAiHistoryRepository.findAll(pageable).map(userAiHistoryMapper::toDto);
    }

    /**
     * Get one userAiHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserAiHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get UserAiHistory : {}", id);
        return userAiHistoryRepository.findById(id).map(userAiHistoryMapper::toDto);
    }

    /**
     * Delete the userAiHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserAiHistory : {}", id);
        userAiHistoryRepository.deleteById(id);
    }

    public int getMaxChatCnt(Long user) {
        return 20;
    }
}
