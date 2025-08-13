package com.shanzha.web.rest;

import com.shanzha.repository.ChapterContentRepository;
import com.shanzha.service.ChapterContentService;
import com.shanzha.service.dto.ChapterContentDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shanzha.domain.ChapterContent}.
 */
@RestController
@RequestMapping("/api/chapter-contents")
public class ChapterContentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterContentResource.class);

    private static final String ENTITY_NAME = "chapterContent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChapterContentService chapterContentService;

    private final ChapterContentRepository chapterContentRepository;

    public ChapterContentResource(ChapterContentService chapterContentService, ChapterContentRepository chapterContentRepository) {
        this.chapterContentService = chapterContentService;
        this.chapterContentRepository = chapterContentRepository;
    }

    /**
     * {@code POST  /chapter-contents} : Create a new chapterContent.
     *
     * @param chapterContentDTO the chapterContentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chapterContentDTO, or with status {@code 400 (Bad Request)} if the chapterContent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChapterContentDTO> createChapterContent(@Valid @RequestBody ChapterContentDTO chapterContentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ChapterContent : {}", chapterContentDTO);
        if (chapterContentDTO.getId() != null) {
            throw new BadRequestAlertException("A new chapterContent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chapterContentDTO = chapterContentService.save(chapterContentDTO);
        return ResponseEntity.created(new URI("/api/chapter-contents/" + chapterContentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, chapterContentDTO.getId().toString()))
            .body(chapterContentDTO);
    }

    /**
     * {@code PUT  /chapter-contents/:id} : Updates an existing chapterContent.
     *
     * @param id the id of the chapterContentDTO to save.
     * @param chapterContentDTO the chapterContentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterContentDTO,
     * or with status {@code 400 (Bad Request)} if the chapterContentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chapterContentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChapterContentDTO> updateChapterContent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChapterContentDTO chapterContentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChapterContent : {}, {}", id, chapterContentDTO);
        if (chapterContentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterContentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chapterContentDTO = chapterContentService.update(chapterContentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterContentDTO.getId().toString()))
            .body(chapterContentDTO);
    }

    /**
     * {@code PATCH  /chapter-contents/:id} : Partial updates given fields of an existing chapterContent, field will ignore if it is null
     *
     * @param id the id of the chapterContentDTO to save.
     * @param chapterContentDTO the chapterContentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterContentDTO,
     * or with status {@code 400 (Bad Request)} if the chapterContentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chapterContentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chapterContentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChapterContentDTO> partialUpdateChapterContent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChapterContentDTO chapterContentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChapterContent partially : {}, {}", id, chapterContentDTO);
        if (chapterContentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterContentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChapterContentDTO> result = chapterContentService.partialUpdate(chapterContentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterContentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /chapter-contents} : get all the chapterContents.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chapterContents in body.
     */
    @GetMapping("")
    public List<ChapterContentDTO> getAllChapterContents(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ChapterContents");
        return chapterContentService.findAll();
    }

    /**
     * {@code GET  /chapter-contents/:id} : get the "id" chapterContent.
     *
     * @param id the id of the chapterContentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chapterContentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChapterContentDTO> getChapterContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChapterContent : {}", id);
        Optional<ChapterContentDTO> chapterContentDTO = chapterContentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chapterContentDTO);
    }

    /**
     * {@code DELETE  /chapter-contents/:id} : delete the "id" chapterContent.
     *
     * @param id the id of the chapterContentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapterContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChapterContent : {}", id);
        chapterContentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
