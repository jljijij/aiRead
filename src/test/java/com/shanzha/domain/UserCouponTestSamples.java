package com.shanzha.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class UserCouponTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserCoupon getUserCouponSample1() {
        return new UserCoupon().id(1L).userId(1L).couponId(1L);
    }

    public static UserCoupon getUserCouponSample2() {
        return new UserCoupon().id(2L).userId(2L).couponId(2L);
    }

    public static UserCoupon getUserCouponRandomSampleGenerator() {
        return new UserCoupon().id(longCount.incrementAndGet()).userId(longCount.incrementAndGet()).couponId(longCount.incrementAndGet());
    }
}
