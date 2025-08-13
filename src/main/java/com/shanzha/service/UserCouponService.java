package com.shanzha.service;

import com.shanzha.domain.*;
import com.shanzha.repository.*;
import com.shanzha.security.SecurityUtils;
import com.shanzha.service.dto.UserCouponDTO;
import com.shanzha.service.dto.UserCouponVO;
import com.shanzha.service.mapper.UserCouponMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shanzha.web.rest.errors.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.UserCoupon}.
 */
@Service
@Transactional
public class UserCouponService {

    ChapterPermissionRepository chapterPermissionRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserCouponService.class);
    CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    NovelRepository novelRepository;

    ChapterRepository chapterRepository;
    private final UserCouponMapper userCouponMapper;

    public UserCouponService(UserCouponRepository userCouponRepository, UserCouponMapper userCouponMapper) {
        this.userCouponRepository = userCouponRepository;
        this.userCouponMapper = userCouponMapper;
    }

    /**
     * Save a userCoupon.
     *
     * @param userCouponDTO the entity to save.
     * @return the persisted entity.
     */
    public UserCouponDTO save(UserCouponDTO userCouponDTO) {
        LOG.debug("Request to save UserCoupon : {}", userCouponDTO);
        UserCoupon userCoupon = userCouponMapper.toEntity(userCouponDTO);
        userCoupon = userCouponRepository.save(userCoupon);
        return userCouponMapper.toDto(userCoupon);
    }

    /**
     * Update a userCoupon.
     *
     * @param userCouponDTO the entity to save.
     * @return the persisted entity.
     */
    public UserCouponDTO update(UserCouponDTO userCouponDTO) {
        LOG.debug("Request to update UserCoupon : {}", userCouponDTO);
        UserCoupon userCoupon = userCouponMapper.toEntity(userCouponDTO);
        userCoupon = userCouponRepository.save(userCoupon);
        return userCouponMapper.toDto(userCoupon);
    }

    /**
     * Partially update a userCoupon.
     *
     * @param userCouponDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserCouponDTO> partialUpdate(UserCouponDTO userCouponDTO) {
        LOG.debug("Request to partially update UserCoupon : {}", userCouponDTO);

        return userCouponRepository
            .findById(userCouponDTO.getId())
            .map(existingUserCoupon -> {
                userCouponMapper.partialUpdate(existingUserCoupon, userCouponDTO);

                return existingUserCoupon;
            })
            .map(userCouponRepository::save)
            .map(userCouponMapper::toDto);
    }

    /**
     * Get all the userCoupons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserCouponDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserCoupons");
        return userCouponRepository.findAll(pageable).map(userCouponMapper::toDto);
    }

    /**
     * Get one userCoupon by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserCouponDTO> findOne(Long id) {
        LOG.debug("Request to get UserCoupon : {}", id);
        return userCouponRepository.findById(id).map(userCouponMapper::toDto);
    }

    /**
     * Delete the userCoupon by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserCoupon : {}", id);
        userCouponRepository.deleteById(id);
    }
    public List<UserCouponVO> getMyCoupons(Long userId, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "usedAt"));
        Page<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId,pageable);

        return userCoupons.stream().map(uc -> {
            Coupon coupon = couponRepository.findById(uc.getCouponId()).orElse(null);
            Novel novel = coupon != null && coupon.getNovelId() != null
                ? novelRepository.findById(coupon.getNovelId()).orElse(null)
                : null;
            Chapter chapter = coupon != null && coupon.getChapterId() != null
                ? chapterRepository.findById(coupon.getChapterId()).orElse(null)
                : null;

            UserCouponVO dto = new UserCouponVO();
            dto.setId(uc.getId());
            dto.setCode(coupon != null ? coupon.getCode() : null);
            dto.setExpiredAt(coupon != null ? coupon.getExpiredAt() : null);
            dto.setIsExpired(coupon != null && coupon.getExpiredAt().isBefore(Instant.now()));
            dto.setUsedAt(uc.getUsedAt());
            dto.setNovelTitle(novel != null ? novel.getTitle() : null);
            dto.setChapterTitle(chapter != null ? chapter.getTitle() : null);
            return dto;
        }).collect(Collectors.toList());
    }
    @Transactional
    public void useCoupon(Long couponId) {
        Optional<Long> userIdOptional = SecurityUtils.getCurrentUserId();
        Long userId = userIdOptional.get();
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(userId, couponId)
            .orElseThrow(() -> new BusinessException(CommonCodeMsg.COUPON_NOT_EXIST));

        if (userCoupon.getUsedAt() != null) {
            throw new BusinessException(CommonCodeMsg.ALREADY_USE);
        }

        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new BusinessException(CommonCodeMsg.COUPON_NOT_EXIST));

        if (coupon.getExpiredAt().isBefore(Instant.now())) {
            throw new BusinessException(CommonCodeMsg.EXPIRE);
        }

        // 添加章节权限
        if (coupon.getChapterId() != null) {
            ChapterPermission perm = new ChapterPermission();
            perm.setUserId(userId);
            perm.setChapterId(coupon.getChapterId());
            perm.setCanRead(true);
            perm.setCanDownload(false);
            perm.setCanComment(false);
            chapterPermissionRepository.save(perm);
        }

        // 更新使用状态
        userCoupon.setUsedAt(Instant.now());
        userCouponRepository.save(userCoupon);
    }

}
