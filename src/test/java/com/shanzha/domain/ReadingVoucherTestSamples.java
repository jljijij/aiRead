package com.shanzha.domain;

import com.shanzha.domain.enumeration.CouponType;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReadingVoucherTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReadingVoucher getReadingVoucherSample1() {
        ReadingVoucher voucher = new ReadingVoucher();
        voucher.setId(1L);
        voucher.setCode("code1");
        voucher.setIssuedAt(Instant.parse("2020-01-01T00:00:00Z"));
        voucher.setExpiresAt(Instant.parse("2020-01-02T00:00:00Z"));
        voucher.setClaimedBy("user1");
        voucher.setType(CouponType.FREE_CHAPTER);
        voucher.setNovelId(1L);
        voucher.setChapterId(1L);
        voucher.setPackageId(1L);
        voucher.setVersion(1L);
        return voucher;
    }

    public static ReadingVoucher getReadingVoucherSample2() {
        ReadingVoucher voucher = new ReadingVoucher();
        voucher.setId(2L);
        voucher.setCode("code2");
        voucher.setIssuedAt(Instant.parse("2020-01-03T00:00:00Z"));
        voucher.setExpiresAt(Instant.parse("2020-01-04T00:00:00Z"));
        voucher.setClaimedBy("user2");
        voucher.setType(CouponType.FREE_PACKAGE);
        voucher.setNovelId(2L);
        voucher.setChapterId(2L);
        voucher.setPackageId(2L);
        voucher.setVersion(2L);
        return voucher;
    }

    public static ReadingVoucher getReadingVoucherRandomSampleGenerator() {
        ReadingVoucher voucher = new ReadingVoucher();
        voucher.setId(longCount.incrementAndGet());
        voucher.setCode(UUID.randomUUID().toString());
        voucher.setIssuedAt(Instant.now());
        voucher.setExpiresAt(Instant.now().plusSeconds(3600));
        voucher.setClaimedBy("user" + longCount.incrementAndGet());
        voucher.setType(CouponType.values()[random.nextInt(CouponType.values().length)]);
        voucher.setNovelId(longCount.incrementAndGet());
        voucher.setChapterId(longCount.incrementAndGet());
        voucher.setPackageId(longCount.incrementAndGet());
        voucher.setVersion(longCount.incrementAndGet());
        return voucher;
    }
}
