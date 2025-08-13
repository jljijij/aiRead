package com.shanzha.web.rest;

import com.shanzha.repository.NovelTagRepository;
import com.shanzha.service.NovelTagService;
import com.shanzha.service.dto.NovelTagDTO;
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
 * REST controller for managing {@link com.shanzha.domain.NovelTag}.
 */
@RestController
@RequestMapping("/api/novel-tags")
public class NovelTagResource {

    private static final Logger LOG = LoggerFactory.getLogger(NovelTagResource.class);

    private static final String ENTITY_NAME = "novelTag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NovelTagService novelTagService;

    private final NovelTagRepository novelTagRepository;

    public NovelTagResource(NovelTagService novelTagService, NovelTagRepository novelTagRepository) {
        this.novelTagService = novelTagService;
        this.novelTagRepository = novelTagRepository;
    }

    /**
     * {@code POST  /novel-tags} : Create a new novelTag.
     *
     * @param novelTagDTO the novelTagDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new novelTagDTO, or with status {@code 400 (Bad Request)} if the novelTag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NovelTagDTO> createNovelTag(@Valid @RequestBody NovelTagDTO novelTagDTO) throws URISyntaxException {
        LOG.debug("REST request to save NovelTag : {}", novelTagDTO);
        if (novelTagDTO.getId() != null) {
            throw new BadRequestAlertException("A new novelTag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        novelTagDTO = novelTagService.save(novelTagDTO);
        return ResponseEntity.created(new URI("/api/novel-tags/" + novelTagDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, novelTagDTO.getId().toString()))
            .body(novelTagDTO);
    }

    /**
     * {@code PUT  /novel-tags/:id} : Updates an existing novelTag.
     *
     * @param id the id of the novelTagDTO to save.
     * @param novelTagDTO the novelTagDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated novelTagDTO,
     * or with status {@code 400 (Bad Request)} if the novelTagDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the novelTagDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NovelTagDTO> updateNovelTag(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NovelTagDTO novelTagDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NovelTag : {}, {}", id, novelTagDTO);
        if (novelTagDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, novelTagDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!novelTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        novelTagDTO = novelTagService.update(novelTagDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, novelTagDTO.getId().toString()))
            .body(novelTagDTO);
    }

    /**
     * {@code PATCH  /novel-tags/:id} : Partial updates given fields of an existing novelTag, field will ignore if it is null
     *
     * @param id the id of the novelTagDTO to save.
     * @param novelTagDTO the novelTagDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated novelTagDTO,
     * or with status {@code 400 (Bad Request)} if the novelTagDTO is not valid,
     * or with status {@code 404 (Not Found)} if the novelTagDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the novelTagDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NovelTagDTO> partialUpdateNovelTag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NovelTagDTO novelTagDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NovelTag partially : {}, {}", id, novelTagDTO);
        if (novelTagDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, novelTagDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!novelTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NovelTagDTO> result = novelTagService.partialUpdate(novelTagDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, novelTagDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /novel-tags} : get all the novelTags.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of novelTags in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NovelTagDTO>> getAllNovelTags(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of NovelTags");
        Page<NovelTagDTO> page = novelTagService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /novel-tags/:id} : get the "id" novelTag.
     *
     * @param id the id of the novelTagDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the novelTagDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NovelTagDTO> getNovelTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to get NovelTag : {}", id);
        Optional<NovelTagDTO> novelTagDTO = novelTagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(novelTagDTO);
    }

    /**
     * {@code DELETE  /novel-tags/:id} : delete the "id" novelTag.
     *
     * @param id the id of the novelTagDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNovelTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete NovelTag : {}", id);
        novelTagService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
