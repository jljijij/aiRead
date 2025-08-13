package com.shanzha.web.rest;

import com.shanzha.repository.ChapterRepository;
import com.shanzha.service.ChapterService;
import com.shanzha.service.dto.ChapterDTO;
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
 * REST controller for managing {@link com.shanzha.domain.Chapter}.
 */
@RestController
@RequestMapping("/api/chapters")
public class ChapterResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterResource.class);

    private static final String ENTITY_NAME = "chapter";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChapterService chapterService;

    private final ChapterRepository chapterRepository;

    public ChapterResource(ChapterService chapterService, ChapterRepository chapterRepository) {
        this.chapterService = chapterService;
        this.chapterRepository = chapterRepository;
    }

    /**
     * {@code POST  /chapters} : Create a new chapter.
     *
     * @param chapterDTO the chapterDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chapterDTO, or with status {@code 400 (Bad Request)} if the chapter has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChapterDTO> createChapter(@Valid @RequestBody ChapterDTO chapterDTO) throws URISyntaxException {
        LOG.debug("REST request to save Chapter : {}", chapterDTO);
        if (chapterDTO.getId() != null) {
            throw new BadRequestAlertException("A new chapter cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chapterDTO = chapterService.save(chapterDTO);
        return ResponseEntity.created(new URI("/api/chapters/" + chapterDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, chapterDTO.getId().toString()))
            .body(chapterDTO);
    }

    /**
     * {@code PUT  /chapters/:id} : Updates an existing chapter.
     *
     * @param id the id of the chapterDTO to save.
     * @param chapterDTO the chapterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterDTO,
     * or with status {@code 400 (Bad Request)} if the chapterDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chapterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChapterDTO> updateChapter(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChapterDTO chapterDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Chapter : {}, {}", id, chapterDTO);
        if (chapterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chapterDTO = chapterService.update(chapterDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterDTO.getId().toString()))
            .body(chapterDTO);
    }

    /**
     * {@code PATCH  /chapters/:id} : Partial updates given fields of an existing chapter, field will ignore if it is null
     *
     * @param id the id of the chapterDTO to save.
     * @param chapterDTO the chapterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterDTO,
     * or with status {@code 400 (Bad Request)} if the chapterDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chapterDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chapterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChapterDTO> partialUpdateChapter(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChapterDTO chapterDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Chapter partially : {}, {}", id, chapterDTO);
        if (chapterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChapterDTO> result = chapterService.partialUpdate(chapterDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /chapters} : get all the chapters.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chapters in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChapterDTO>> getAllChapters(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Chapters");
        Page<ChapterDTO> page;
        if (eagerload) {
            page = chapterService.findAllWithEagerRelationships(pageable);
        } else {
            page = chapterService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /chapters/:id} : get the "id" chapter.
     *
     * @param id the id of the chapterDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chapterDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChapterDTO> getChapter(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Chapter : {}", id);
        Optional<ChapterDTO> chapterDTO = chapterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chapterDTO);
    }

    /**
     * {@code DELETE  /chapters/:id} : delete the "id" chapter.
     *
     * @param id the id of the chapterDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Chapter : {}", id);
        chapterService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
