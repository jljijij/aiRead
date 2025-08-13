package com.shanzha.web.rest;

import com.shanzha.repository.UserFootRepository;
import com.shanzha.security.SecurityUtils;
import com.shanzha.service.UserFootService;
import com.shanzha.service.dto.NovelUserFootVO;
import com.shanzha.service.dto.UserFootDTO;
import com.shanzha.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shanzha.domain.UserFoot}.
 */
@RestController
@RequestMapping("/api/user-foots")
public class UserFootResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserFootResource.class);

    private static final String ENTITY_NAME = "userFoot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserFootService userFootService;

    private final UserFootRepository userFootRepository;

    public UserFootResource(UserFootService userFootService, UserFootRepository userFootRepository) {
        this.userFootService = userFootService;
        this.userFootRepository = userFootRepository;
    }

    /**
     * {@code POST  /user-foots} : Create a new userFoot.
     *
     * @param userFootDTO the userFootDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userFootDTO, or with status {@code 400 (Bad Request)} if the userFoot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserFootDTO> createUserFoot(@Valid @RequestBody UserFootDTO userFootDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserFoot : {}", userFootDTO);
        if (userFootDTO.getId() != null) {
            throw new BadRequestAlertException("A new userFoot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userFootDTO = userFootService.save(userFootDTO);
        return ResponseEntity.created(new URI("/api/user-foots/" + userFootDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userFootDTO.getId().toString()))
            .body(userFootDTO);
    }

    /**
     * {@code PUT  /user-foots/:id} : Updates an existing userFoot.
     *
     * @param id the id of the userFootDTO to save.
     * @param userFootDTO the userFootDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userFootDTO,
     * or with status {@code 400 (Bad Request)} if the userFootDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userFootDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserFootDTO> updateUserFoot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserFootDTO userFootDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserFoot : {}, {}", id, userFootDTO);
        if (userFootDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userFootDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userFootRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userFootDTO = userFootService.update(userFootDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userFootDTO.getId().toString()))
            .body(userFootDTO);
    }

    /**
     * {@code PATCH  /user-foots/:id} : Partial updates given fields of an existing userFoot, field will ignore if it is null
     *
     * @param id the id of the userFootDTO to save.
     * @param userFootDTO the userFootDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userFootDTO,
     * or with status {@code 400 (Bad Request)} if the userFootDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userFootDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userFootDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserFootDTO> partialUpdateUserFoot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserFootDTO userFootDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserFoot partially : {}, {}", id, userFootDTO);
        if (userFootDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userFootDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userFootRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserFootDTO> result = userFootService.partialUpdate(userFootDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userFootDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-foots} : get all the userFoots.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userFoots in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserFootDTO>> getAllUserFoots(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UserFoots");
        Page<UserFootDTO> page = userFootService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-foots/:id} : get the "id" userFoot.
     *
     * @param id the id of the userFootDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userFootDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserFootDTO> getUserFoot(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserFoot : {}", id);
        Optional<UserFootDTO> userFootDTO = userFootService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userFootDTO);
    }

    /**
     * {@code DELETE  /user-foots/:id} : delete the "id" userFoot.
     *
     * @param id the id of the userFootDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserFoot(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserFoot : {}", id);
        userFootService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @Operation(summary = "获取用户对某篇文章的点赞和收藏状态")
    @GetMapping("/{articleId}")
    public ResponseEntity<NovelUserFootVO> getUserArticleFoot(
        @Parameter(description = "文章 ID") @PathVariable Long articleId) {

        Long userId = SecurityUtils.getCurrentUserId().orElse(null);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        NovelUserFootVO vo = userFootService.getUserArticleFoot(userId, articleId);
        return ResponseEntity.ok(vo);
    }

    @Operation(summary = "获取当前用户点赞过的文章 ID 列表")
    @GetMapping("/praise")
    public ResponseEntity<List<Long>> listPraisedArticles() {
        Long userId = SecurityUtils.getCurrentUserId().orElse(null);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userFootService.listPraisedArticleIds(userId));
    }

    @Operation(summary = "获取当前用户收藏过的文章 ID 列表")
    @GetMapping("/collect")
    public ResponseEntity<List<Long>> listCollectedArticles() {
        Long userId = SecurityUtils.getCurrentUserId().orElse(null);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userFootService.listCollectedArticleIds(userId));
    }

}
