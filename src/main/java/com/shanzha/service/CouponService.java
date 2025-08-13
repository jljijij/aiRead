package com.shanzha.service;

import com.shanzha.domain.Coupon;
import com.shanzha.repository.CouponRepository;
import com.shanzha.service.dto.CouponDTO;
import com.shanzha.service.mapper.CouponMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.Coupon}.
 */
@Service
@Transactional
public class CouponService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;

    private final CouponMapper couponMapper;

    public CouponService(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    /**
     * Save a coupon.
     *
     * @param couponDTO the entity to save.
     * @return the persisted entity.
     */
    public CouponDTO save(CouponDTO couponDTO) {
        LOG.debug("Request to save Coupon : {}", couponDTO);
        Coupon coupon = couponMapper.toEntity(couponDTO);
        coupon = couponRepository.save(coupon);
        return couponMapper.toDto(coupon);
    }

    /**
     * Update a coupon.
     *
     * @param couponDTO the entity to save.
     * @return the persisted entity.
     */
    public CouponDTO update(CouponDTO couponDTO) {
        LOG.debug("Request to update Coupon : {}", couponDTO);
        Coupon coupon = couponMapper.toEntity(couponDTO);
        coupon = couponRepository.save(coupon);
        return couponMapper.toDto(coupon);
    }

    /**
     * Partially update a coupon.
     *
     * @param couponDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CouponDTO> partialUpdate(CouponDTO couponDTO) {
        LOG.debug("Request to partially update Coupon : {}", couponDTO);

        return couponRepository
            .findById(couponDTO.getId())
            .map(existingCoupon -> {
                couponMapper.partialUpdate(existingCoupon, couponDTO);

                return existingCoupon;
            })
            .map(couponRepository::save)
            .map(couponMapper::toDto);
    }

    /**
     * Get all the coupons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CouponDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Coupons");
        return couponRepository.findAll(pageable).map(couponMapper::toDto);
    }

    /**
     * Get one coupon by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CouponDTO> findOne(Long id) {
        LOG.debug("Request to get Coupon : {}", id);
        return couponRepository.findById(id).map(couponMapper::toDto);
    }

    /**
     * Delete the coupon by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Coupon : {}", id);
        couponRepository.deleteById(id);
    }


}
