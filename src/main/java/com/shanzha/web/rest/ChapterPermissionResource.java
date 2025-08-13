package com.shanzha.web.rest;

import com.shanzha.repository.ChapterPermissionRepository;
import com.shanzha.service.ChapterPermissionService;
import com.shanzha.service.dto.ChapterPermissionDTO;
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
 * REST controller for managing {@link com.shanzha.domain.ChapterPermission}.
 */
@RestController
@RequestMapping("/api/chapter-permissions")
public class ChapterPermissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterPermissionResource.class);

    private static final String ENTITY_NAME = "chapterPermission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChapterPermissionService chapterPermissionService;

    private final ChapterPermissionRepository chapterPermissionRepository;

    public ChapterPermissionResource(
        ChapterPermissionService chapterPermissionService,
        ChapterPermissionRepository chapterPermissionRepository
    ) {
        this.chapterPermissionService = chapterPermissionService;
        this.chapterPermissionRepository = chapterPermissionRepository;
    }

    /**
     * {@code POST  /chapter-permissions} : Create a new chapterPermission.
     *
     * @param chapterPermissionDTO the chapterPermissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chapterPermissionDTO, or with status {@code 400 (Bad Request)} if the chapterPermission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChapterPermissionDTO> createChapterPermission(@Valid @RequestBody ChapterPermissionDTO chapterPermissionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ChapterPermission : {}", chapterPermissionDTO);
        if (chapterPermissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new chapterPermission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chapterPermissionDTO = chapterPermissionService.save(chapterPermissionDTO);
        return ResponseEntity.created(new URI("/api/chapter-permissions/" + chapterPermissionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, chapterPermissionDTO.getId().toString()))
            .body(chapterPermissionDTO);
    }

    /**
     * {@code PUT  /chapter-permissions/:id} : Updates an existing chapterPermission.
     *
     * @param id the id of the chapterPermissionDTO to save.
     * @param chapterPermissionDTO the chapterPermissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterPermissionDTO,
     * or with status {@code 400 (Bad Request)} if the chapterPermissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chapterPermissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChapterPermissionDTO> updateChapterPermission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChapterPermissionDTO chapterPermissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChapterPermission : {}, {}", id, chapterPermissionDTO);
        if (chapterPermissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterPermissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterPermissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chapterPermissionDTO = chapterPermissionService.update(chapterPermissionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterPermissionDTO.getId().toString()))
            .body(chapterPermissionDTO);
    }

    /**
     * {@code PATCH  /chapter-permissions/:id} : Partial updates given fields of an existing chapterPermission, field will ignore if it is null
     *
     * @param id the id of the chapterPermissionDTO to save.
     * @param chapterPermissionDTO the chapterPermissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterPermissionDTO,
     * or with status {@code 400 (Bad Request)} if the chapterPermissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chapterPermissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chapterPermissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChapterPermissionDTO> partialUpdateChapterPermission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChapterPermissionDTO chapterPermissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChapterPermission partially : {}, {}", id, chapterPermissionDTO);
        if (chapterPermissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterPermissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterPermissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChapterPermissionDTO> result = chapterPermissionService.partialUpdate(chapterPermissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterPermissionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /chapter-permissions} : get all the chapterPermissions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chapterPermissions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChapterPermissionDTO>> getAllChapterPermissions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ChapterPermissions");
        Page<ChapterPermissionDTO> page = chapterPermissionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /chapter-permissions/:id} : get the "id" chapterPermission.
     *
     * @param id the id of the chapterPermissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chapterPermissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChapterPermissionDTO> getChapterPermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChapterPermission : {}", id);
        Optional<ChapterPermissionDTO> chapterPermissionDTO = chapterPermissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chapterPermissionDTO);
    }

    /**
     * {@code DELETE  /chapter-permissions/:id} : delete the "id" chapterPermission.
     *
     * @param id the id of the chapterPermissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapterPermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChapterPermission : {}", id);
        chapterPermissionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
