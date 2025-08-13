package com.shanzha.web.rest;

import com.shanzha.domain.CommonCodeMsg;
import com.shanzha.repository.UserCouponRepository;
import com.shanzha.security.SecurityUtils;
import com.shanzha.service.UserCouponService;
import com.shanzha.service.dto.UseCouponParam;
import com.shanzha.service.dto.UserCouponDTO;
import com.shanzha.service.dto.UserCouponVO;
import com.shanzha.web.rest.errors.BadRequestAlertException;
import com.shanzha.web.rest.errors.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
 * REST controller for managing {@link com.shanzha.domain.UserCoupon}.
 */
@RestController
@RequestMapping("/api/user-coupons")
public class UserCouponResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserCouponResource.class);

    private static final String ENTITY_NAME = "userCoupon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserCouponService userCouponService;

    private final UserCouponRepository userCouponRepository;

    public UserCouponResource(UserCouponService userCouponService, UserCouponRepository userCouponRepository) {
        this.userCouponService = userCouponService;
        this.userCouponRepository = userCouponRepository;
    }

    /**
     * {@code POST  /user-coupons} : Create a new userCoupon.
     *
     * @param userCouponDTO the userCouponDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userCouponDTO, or with status {@code 400 (Bad Request)} if the userCoupon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserCouponDTO> createUserCoupon(@Valid @RequestBody UserCouponDTO userCouponDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserCoupon : {}", userCouponDTO);
        if (userCouponDTO.getId() != null) {
            throw new BadRequestAlertException("A new userCoupon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userCouponDTO = userCouponService.save(userCouponDTO);
        return ResponseEntity.created(new URI("/api/user-coupons/" + userCouponDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userCouponDTO.getId().toString()))
            .body(userCouponDTO);
    }

    /**
     * {@code PUT  /user-coupons/:id} : Updates an existing userCoupon.
     *
     * @param id the id of the userCouponDTO to save.
     * @param userCouponDTO the userCouponDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userCouponDTO,
     * or with status {@code 400 (Bad Request)} if the userCouponDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userCouponDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserCouponDTO> updateUserCoupon(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserCouponDTO userCouponDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserCoupon : {}, {}", id, userCouponDTO);
        if (userCouponDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userCouponDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userCouponRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userCouponDTO = userCouponService.update(userCouponDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userCouponDTO.getId().toString()))
            .body(userCouponDTO);
    }

    /**
     * {@code PATCH  /user-coupons/:id} : Partial updates given fields of an existing userCoupon, field will ignore if it is null
     *
     * @param id the id of the userCouponDTO to save.
     * @param userCouponDTO the userCouponDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userCouponDTO,
     * or with status {@code 400 (Bad Request)} if the userCouponDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userCouponDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userCouponDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserCouponDTO> partialUpdateUserCoupon(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserCouponDTO userCouponDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserCoupon partially : {}, {}", id, userCouponDTO);
        if (userCouponDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userCouponDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userCouponRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserCouponDTO> result = userCouponService.partialUpdate(userCouponDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userCouponDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-coupons} : get all the userCoupons.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userCoupons in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserCouponDTO>> getAllUserCoupons(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UserCoupons");
        Page<UserCouponDTO> page = userCouponService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-coupons/:id} : get the "id" userCoupon.
     *
     * @param id the id of the userCouponDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userCouponDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserCouponDTO> getUserCoupon(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserCoupon : {}", id);
        Optional<UserCouponDTO> userCouponDTO = userCouponService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userCouponDTO);
    }

    /**
     * {@code DELETE  /user-coupons/:id} : delete the "id" userCoupon.
     *
     * @param id the id of the userCouponDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCoupon(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserCoupon : {}", id);
        userCouponService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserCouponVO>> getMyCoupons(@RequestParam(defaultValue = "0") int page) {
        Optional<Long> userIdOption = SecurityUtils.getCurrentUserId(); // 假设你有这个方法
        if(userIdOption.isEmpty()) {
            throw new BusinessException(CommonCodeMsg.NOT_LOGIN);
        }
        Long userId = userIdOption.get();
        List<UserCouponVO> result = userCouponService.getMyCoupons(userId,page);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/use")
    public ResponseEntity<?> useCoupon(@RequestBody UseCouponParam param) {
        userCouponService.useCoupon(param.getCouponId());
        return ResponseEntity.ok(Map.of("success", true, "message", "章节权限已解锁"));
    }

}
