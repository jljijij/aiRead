package com.shanzha.web.rest;

import com.shanzha.repository.ChapterPackageRepository;
import com.shanzha.service.ChapterPackageService;
import com.shanzha.service.dto.ChapterPackageDTO;
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
 * REST controller for managing {@link com.shanzha.domain.ChapterPackage}.
 */
@RestController
@RequestMapping("/api/chapter-packages")
public class ChapterPackageResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterPackageResource.class);

    private static final String ENTITY_NAME = "chapterPackage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChapterPackageService chapterPackageService;

    private final ChapterPackageRepository chapterPackageRepository;

    public ChapterPackageResource(ChapterPackageService chapterPackageService, ChapterPackageRepository chapterPackageRepository) {
        this.chapterPackageService = chapterPackageService;
        this.chapterPackageRepository = chapterPackageRepository;
    }

    /**
     * {@code POST  /chapter-packages} : Create a new chapterPackage.
     *
     * @param chapterPackageDTO the chapterPackageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chapterPackageDTO, or with status {@code 400 (Bad Request)} if the chapterPackage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChapterPackageDTO> createChapterPackage(@Valid @RequestBody ChapterPackageDTO chapterPackageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ChapterPackage : {}", chapterPackageDTO);
        if (chapterPackageDTO.getId() != null) {
            throw new BadRequestAlertException("A new chapterPackage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chapterPackageDTO = chapterPackageService.save(chapterPackageDTO);
        return ResponseEntity.created(new URI("/api/chapter-packages/" + chapterPackageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, chapterPackageDTO.getId().toString()))
            .body(chapterPackageDTO);
    }

    /**
     * {@code PUT  /chapter-packages/:id} : Updates an existing chapterPackage.
     *
     * @param id the id of the chapterPackageDTO to save.
     * @param chapterPackageDTO the chapterPackageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterPackageDTO,
     * or with status {@code 400 (Bad Request)} if the chapterPackageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chapterPackageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChapterPackageDTO> updateChapterPackage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChapterPackageDTO chapterPackageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChapterPackage : {}, {}", id, chapterPackageDTO);
        if (chapterPackageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterPackageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterPackageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chapterPackageDTO = chapterPackageService.update(chapterPackageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterPackageDTO.getId().toString()))
            .body(chapterPackageDTO);
    }

    /**
     * {@code PATCH  /chapter-packages/:id} : Partial updates given fields of an existing chapterPackage, field will ignore if it is null
     *
     * @param id the id of the chapterPackageDTO to save.
     * @param chapterPackageDTO the chapterPackageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterPackageDTO,
     * or with status {@code 400 (Bad Request)} if the chapterPackageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chapterPackageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chapterPackageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChapterPackageDTO> partialUpdateChapterPackage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChapterPackageDTO chapterPackageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChapterPackage partially : {}, {}", id, chapterPackageDTO);
        if (chapterPackageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterPackageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterPackageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChapterPackageDTO> result = chapterPackageService.partialUpdate(chapterPackageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterPackageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /chapter-packages} : get all the chapterPackages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chapterPackages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChapterPackageDTO>> getAllChapterPackages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ChapterPackages");
        Page<ChapterPackageDTO> page = chapterPackageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /chapter-packages/:id} : get the "id" chapterPackage.
     *
     * @param id the id of the chapterPackageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chapterPackageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChapterPackageDTO> getChapterPackage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChapterPackage : {}", id);
        Optional<ChapterPackageDTO> chapterPackageDTO = chapterPackageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chapterPackageDTO);
    }

    /**
     * {@code DELETE  /chapter-packages/:id} : delete the "id" chapterPackage.
     *
     * @param id the id of the chapterPackageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapterPackage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChapterPackage : {}", id);
        chapterPackageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
