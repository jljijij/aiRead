package com.shanzha.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserFootTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UserFoot getUserFootSample1() {
        return new UserFoot()
            .id(1L)
            .userId(1L)
            .documentId(1L)
            .documentUserId(1L)
            .documentType(1)
            .readStat(1)
            .praiseStat(1)
            .collectionStat(1)
            .commentStat(1);
    }

    public static UserFoot getUserFootSample2() {
        return new UserFoot()
            .id(2L)
            .userId(2L)
            .documentId(2L)
            .documentUserId(2L)
            .documentType(2)
            .readStat(2)
            .praiseStat(2)
            .collectionStat(2)
            .commentStat(2);
    }

    public static UserFoot getUserFootRandomSampleGenerator() {
        return new UserFoot()
            .id(longCount.incrementAndGet())
            .userId(longCount.incrementAndGet())
            .documentId(longCount.incrementAndGet())
            .documentUserId(longCount.incrementAndGet())
            .documentType(intCount.incrementAndGet())
            .readStat(intCount.incrementAndGet())
            .praiseStat(intCount.incrementAndGet())
            .collectionStat(intCount.incrementAndGet())
            .commentStat(intCount.incrementAndGet());
    }
}
