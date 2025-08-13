package com.shanzha.service;

import com.shanzha.domain.NotifyMsg;
import com.shanzha.repository.NotifyMsgRepository;
import com.shanzha.service.dto.NotifyMsgDTO;
import com.shanzha.service.mapper.NotifyMsgMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.NotifyMsg}.
 */
@Service
@Transactional
public class NotifyMsgService {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyMsgService.class);
    @Autowired
    private final NotifyMsgRepository notifyMsgRepository;
    @Autowired
    private final NotifyMsgMapper notifyMsgMapper;

    public NotifyMsgService(NotifyMsgRepository notifyMsgRepository, NotifyMsgMapper notifyMsgMapper) {
        this.notifyMsgRepository = notifyMsgRepository;
        this.notifyMsgMapper = notifyMsgMapper;
    }

    /**
     * Save a notifyMsg.
     *
     * @param notifyMsgDTO the entity to save.
     * @return the persisted entity.
     */
    public NotifyMsgDTO save(NotifyMsgDTO notifyMsgDTO) {
        LOG.debug("Request to save NotifyMsg : {}", notifyMsgDTO);
        NotifyMsg notifyMsg = notifyMsgMapper.toEntity(notifyMsgDTO);
        notifyMsg = notifyMsgRepository.save(notifyMsg);
        return notifyMsgMapper.toDto(notifyMsg);
    }

    /**
     * Update a notifyMsg.
     *
     * @param notifyMsgDTO the entity to save.
     * @return the persisted entity.
     */
    public NotifyMsgDTO update(NotifyMsgDTO notifyMsgDTO) {
        LOG.debug("Request to update NotifyMsg : {}", notifyMsgDTO);
        NotifyMsg notifyMsg = notifyMsgMapper.toEntity(notifyMsgDTO);
        notifyMsg = notifyMsgRepository.save(notifyMsg);
        return notifyMsgMapper.toDto(notifyMsg);
    }

    /**
     * Partially update a notifyMsg.
     *
     * @param notifyMsgDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NotifyMsgDTO> partialUpdate(NotifyMsgDTO notifyMsgDTO) {
        LOG.debug("Request to partially update NotifyMsg : {}", notifyMsgDTO);

        return notifyMsgRepository
            .findById(notifyMsgDTO.getId())
            .map(existingNotifyMsg -> {
                notifyMsgMapper.partialUpdate(existingNotifyMsg, notifyMsgDTO);

                return existingNotifyMsg;
            })
            .map(notifyMsgRepository::save)
            .map(notifyMsgMapper::toDto);
    }

    /**
     * Get all the notifyMsgs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NotifyMsgDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NotifyMsgs");
        return notifyMsgRepository.findAll(pageable).map(notifyMsgMapper::toDto);
    }

    /**
     * Get one notifyMsg by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NotifyMsgDTO> findOne(Long id) {
        LOG.debug("Request to get NotifyMsg : {}", id);
        return notifyMsgRepository.findById(id).map(notifyMsgMapper::toDto);
    }

    /**
     * Delete the notifyMsg by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete NotifyMsg : {}", id);
        notifyMsgRepository.deleteById(id);
    }
}
