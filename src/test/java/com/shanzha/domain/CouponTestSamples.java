package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CouponTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Coupon getCouponSample1() {
        return new Coupon().id(1L).code("code1").novelId(1L).chapterId(1L).packageId(1L);
    }

    public static Coupon getCouponSample2() {
        return new Coupon().id(2L).code("code2").novelId(2L).chapterId(2L).packageId(2L);
    }

    public static Coupon getCouponRandomSampleGenerator() {
        return new Coupon()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .novelId(longCount.incrementAndGet())
            .chapterId(longCount.incrementAndGet())
            .packageId(longCount.incrementAndGet());
    }
}
