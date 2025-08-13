package com.shanzha.web.rest;

import com.shanzha.repository.NotifyMsgRepository;
import com.shanzha.service.NotifyMsgService;
import com.shanzha.service.dto.NotifyMsgDTO;
import com.shanzha.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shanzha.domain.NotifyMsg}.
 */
@RestController
@RequestMapping("/api/notify-msgs")
public class NotifyMsgResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyMsgResource.class);

    private static final String ENTITY_NAME = "notifyMsg";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotifyMsgService notifyMsgService;

    private final NotifyMsgRepository notifyMsgRepository;

    public NotifyMsgResource(NotifyMsgService notifyMsgService, NotifyMsgRepository notifyMsgRepository) {
        this.notifyMsgService = notifyMsgService;
        this.notifyMsgRepository = notifyMsgRepository;
    }

    /**
     * {@code POST  /notify-msgs} : Create a new notifyMsg.
     *
     * @param notifyMsgDTO the notifyMsgDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notifyMsgDTO, or with status {@code 400 (Bad Request)} if the notifyMsg has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotifyMsgDTO> createNotifyMsg(@Valid @RequestBody NotifyMsgDTO notifyMsgDTO) throws URISyntaxException {
        LOG.debug("REST request to save NotifyMsg : {}", notifyMsgDTO);
        if (notifyMsgDTO.getId() != null) {
            throw new BadRequestAlertException("A new notifyMsg cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notifyMsgDTO = notifyMsgService.save(notifyMsgDTO);
        return ResponseEntity.created(new URI("/api/notify-msgs/" + notifyMsgDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, notifyMsgDTO.getId().toString()))
            .body(notifyMsgDTO);
    }

    /**
     * {@code PUT  /notify-msgs/:id} : Updates an existing notifyMsg.
     *
     * @param id the id of the notifyMsgDTO to save.
     * @param notifyMsgDTO the notifyMsgDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notifyMsgDTO,
     * or with status {@code 400 (Bad Request)} if the notifyMsgDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notifyMsgDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotifyMsgDTO> updateNotifyMsg(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotifyMsgDTO notifyMsgDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NotifyMsg : {}, {}", id, notifyMsgDTO);
        if (notifyMsgDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notifyMsgDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notifyMsgRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notifyMsgDTO = notifyMsgService.update(notifyMsgDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, notifyMsgDTO.getId().toString()))
            .body(notifyMsgDTO);
    }

    /**
     * {@code PATCH  /notify-msgs/:id} : Partial updates given fields of an existing notifyMsg, field will ignore if it is null
     *
     * @param id the id of the notifyMsgDTO to save.
     * @param notifyMsgDTO the notifyMsgDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notifyMsgDTO,
     * or with status {@code 400 (Bad Request)} if the notifyMsgDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notifyMsgDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notifyMsgDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotifyMsgDTO> partialUpdateNotifyMsg(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotifyMsgDTO notifyMsgDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NotifyMsg partially : {}, {}", id, notifyMsgDTO);
        if (notifyMsgDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notifyMsgDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notifyMsgRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotifyMsgDTO> result = notifyMsgService.partialUpdate(notifyMsgDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, notifyMsgDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notify-msgs} : get all the notifyMsgs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifyMsgs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NotifyMsgDTO>> getAllNotifyMsgs(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of NotifyMsgs");
        Page<NotifyMsgDTO> page = notifyMsgService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notify-msgs/:id} : get the "id" notifyMsg.
     *
     * @param id the id of the notifyMsgDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notifyMsgDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotifyMsgDTO> getNotifyMsg(@PathVariable("id") Long id) {
        LOG.debug("REST request to get NotifyMsg : {}", id);
        Optional<NotifyMsgDTO> notifyMsgDTO = notifyMsgService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notifyMsgDTO);
    }

    /**
     * {@code DELETE  /notify-msgs/:id} : delete the "id" notifyMsg.
     *
     * @param id the id of the notifyMsgDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotifyMsg(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete NotifyMsg : {}", id);
        notifyMsgService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
