package com.shanzha.service;

import com.shanzha.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CouponScheduler {

    private final UserRepository userRepository;
    private final CouponService couponService;

    public CouponScheduler(UserRepository userRepository, CouponService couponService) {
        this.userRepository = userRepository;
        this.couponService = couponService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void distributeDailyCoupons() {
        userRepository.findAll().forEach(couponService::issueTo);
    }
}
