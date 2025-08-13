package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChapterPackageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChapterPackage getChapterPackageSample1() {
        return new ChapterPackage().id(1L).name("name1").description("description1");
    }

    public static ChapterPackage getChapterPackageSample2() {
        return new ChapterPackage().id(2L).name("name2").description("description2");
    }

    public static ChapterPackage getChapterPackageRandomSampleGenerator() {
        return new ChapterPackage()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
