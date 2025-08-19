package com.shanzha.service;

import com.shanzha.domain.Coupon;
import com.shanzha.domain.User;
import com.shanzha.repository.CouponRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Cacheable(value = com.shanzha.domain.Coupon.class.getName(), key = "#id")
    public Coupon findOne(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = com.shanzha.domain.Coupon.class.getName(), key = "#coupon.id")
    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon issueTo(User user) {
        Coupon coupon = new Coupon();
        coupon.setCode(UUID.randomUUID().toString());
        coupon.setIssuedAt(Instant.now());
        coupon.setUser(user);
        return save(coupon);
    }
}
