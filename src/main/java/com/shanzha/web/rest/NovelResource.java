package com.shanzha.web.rest;

import com.shanzha.repository.NovelRepository;
import com.shanzha.service.NovelService;
import com.shanzha.service.dto.NovelDTO;
import com.shanzha.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shanzha.domain.Novel}.
 */
@RestController
@RequestMapping("/api/novels")
public class NovelResource {

    private static final Logger LOG = LoggerFactory.getLogger(NovelResource.class);

    private static final String ENTITY_NAME = "novel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NovelService novelService;

    private final NovelRepository novelRepository;

    public NovelResource(NovelService novelService, NovelRepository novelRepository) {
        this.novelService = novelService;
        this.novelRepository = novelRepository;
    }

    /**
     * {@code POST  /novels} : Create a new novel.
     *
     * @param novelDTO the novelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new novelDTO, or with status {@code 400 (Bad Request)} if the novel has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NovelDTO> createNovel(@Valid @RequestBody NovelDTO novelDTO) throws URISyntaxException {
        LOG.debug("REST request to save Novel : {}", novelDTO);
        if (novelDTO.getId() != null) {
            throw new BadRequestAlertException("A new novel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        novelDTO = novelService.save(novelDTO);
        return ResponseEntity.created(new URI("/api/novels/" + novelDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, novelDTO.getId().toString()))
            .body(novelDTO);
    }

    /**
     * {@code PUT  /novels/:id} : Updates an existing novel.
     *
     * @param id the id of the novelDTO to save.
     * @param novelDTO the novelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated novelDTO,
     * or with status {@code 400 (Bad Request)} if the novelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the novelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NovelDTO> updateNovel(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NovelDTO novelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Novel : {}, {}", id, novelDTO);
        if (novelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, novelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!novelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        novelDTO = novelService.update(novelDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, novelDTO.getId().toString()))
            .body(novelDTO);
    }

    /**
     * {@code PATCH  /novels/:id} : Partial updates given fields of an existing novel, field will ignore if it is null
     *
     * @param id the id of the novelDTO to save.
     * @param novelDTO the novelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated novelDTO,
     * or with status {@code 400 (Bad Request)} if the novelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the novelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the novelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NovelDTO> partialUpdateNovel(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NovelDTO novelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Novel partially : {}, {}", id, novelDTO);
        if (novelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, novelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!novelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NovelDTO> result = novelService.partialUpdate(novelDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, novelDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /novels} : get all the novels.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of novels in body.
     */
    @GetMapping("")
    public List<NovelDTO> getAllNovels() {
        LOG.debug("REST request to get all Novels");
        return novelService.findAll();
    }

    /**
     * {@code GET  /novels/:id} : get the "id" novel.
     *
     * @param id the id of the novelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the novelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NovelDTO> getNovel(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Novel : {}", id);
        Optional<NovelDTO> novelDTO = novelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(novelDTO);
    }

    /**
     * {@code DELETE  /novels/:id} : delete the "id" novel.
     *
     * @param id the id of the novelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNovel(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Novel : {}", id);
        novelService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
    @PostMapping("/parse")
    public ResponseEntity<String> parse(@RequestParam("file") MultipartFile file) throws IOException {
        String content = novelService.parseFile(file);
        return ResponseEntity.ok(content);
    }
}
