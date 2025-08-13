package com.shanzha.web.rest;

import com.shanzha.repository.ChapterPackageItemRepository;
import com.shanzha.service.ChapterPackageItemService;
import com.shanzha.service.dto.ChapterPackageItemDTO;
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
 * REST controller for managing {@link com.shanzha.domain.ChapterPackageItem}.
 */
@RestController
@RequestMapping("/api/chapter-package-items")
public class ChapterPackageItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterPackageItemResource.class);

    private static final String ENTITY_NAME = "chapterPackageItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChapterPackageItemService chapterPackageItemService;

    private final ChapterPackageItemRepository chapterPackageItemRepository;

    public ChapterPackageItemResource(
        ChapterPackageItemService chapterPackageItemService,
        ChapterPackageItemRepository chapterPackageItemRepository
    ) {
        this.chapterPackageItemService = chapterPackageItemService;
        this.chapterPackageItemRepository = chapterPackageItemRepository;
    }

    /**
     * {@code POST  /chapter-package-items} : Create a new chapterPackageItem.
     *
     * @param chapterPackageItemDTO the chapterPackageItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chapterPackageItemDTO, or with status {@code 400 (Bad Request)} if the chapterPackageItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChapterPackageItemDTO> createChapterPackageItem(@Valid @RequestBody ChapterPackageItemDTO chapterPackageItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ChapterPackageItem : {}", chapterPackageItemDTO);
        if (chapterPackageItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new chapterPackageItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chapterPackageItemDTO = chapterPackageItemService.save(chapterPackageItemDTO);
        return ResponseEntity.created(new URI("/api/chapter-package-items/" + chapterPackageItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, chapterPackageItemDTO.getId().toString()))
            .body(chapterPackageItemDTO);
    }

    /**
     * {@code PUT  /chapter-package-items/:id} : Updates an existing chapterPackageItem.
     *
     * @param id the id of the chapterPackageItemDTO to save.
     * @param chapterPackageItemDTO the chapterPackageItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterPackageItemDTO,
     * or with status {@code 400 (Bad Request)} if the chapterPackageItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chapterPackageItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChapterPackageItemDTO> updateChapterPackageItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChapterPackageItemDTO chapterPackageItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChapterPackageItem : {}, {}", id, chapterPackageItemDTO);
        if (chapterPackageItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterPackageItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterPackageItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chapterPackageItemDTO = chapterPackageItemService.update(chapterPackageItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterPackageItemDTO.getId().toString()))
            .body(chapterPackageItemDTO);
    }

    /**
     * {@code PATCH  /chapter-package-items/:id} : Partial updates given fields of an existing chapterPackageItem, field will ignore if it is null
     *
     * @param id the id of the chapterPackageItemDTO to save.
     * @param chapterPackageItemDTO the chapterPackageItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chapterPackageItemDTO,
     * or with status {@code 400 (Bad Request)} if the chapterPackageItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chapterPackageItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chapterPackageItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChapterPackageItemDTO> partialUpdateChapterPackageItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChapterPackageItemDTO chapterPackageItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChapterPackageItem partially : {}, {}", id, chapterPackageItemDTO);
        if (chapterPackageItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chapterPackageItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chapterPackageItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChapterPackageItemDTO> result = chapterPackageItemService.partialUpdate(chapterPackageItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chapterPackageItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /chapter-package-items} : get all the chapterPackageItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chapterPackageItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChapterPackageItemDTO>> getAllChapterPackageItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ChapterPackageItems");
        Page<ChapterPackageItemDTO> page = chapterPackageItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /chapter-package-items/:id} : get the "id" chapterPackageItem.
     *
     * @param id the id of the chapterPackageItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chapterPackageItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChapterPackageItemDTO> getChapterPackageItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChapterPackageItem : {}", id);
        Optional<ChapterPackageItemDTO> chapterPackageItemDTO = chapterPackageItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chapterPackageItemDTO);
    }

    /**
     * {@code DELETE  /chapter-package-items/:id} : delete the "id" chapterPackageItem.
     *
     * @param id the id of the chapterPackageItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapterPackageItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChapterPackageItem : {}", id);
        chapterPackageItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
