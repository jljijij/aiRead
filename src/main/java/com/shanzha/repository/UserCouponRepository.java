package com.shanzha.repository;

import org.springframework.data.domain.Page;
import com.shanzha.domain.UserCoupon;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Spring Data JPA repository for the UserCoupon entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Page<UserCoupon> findByUserId(Long userId, Pageable pageable);

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
}
