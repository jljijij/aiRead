package com.shanzha.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ChapterPackageItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChapterPackageItem getChapterPackageItemSample1() {
        return new ChapterPackageItem().id(1L).packageId(1L).chapterId(1L);
    }

    public static ChapterPackageItem getChapterPackageItemSample2() {
        return new ChapterPackageItem().id(2L).packageId(2L).chapterId(2L);
    }

    public static ChapterPackageItem getChapterPackageItemRandomSampleGenerator() {
        return new ChapterPackageItem()
            .id(longCount.incrementAndGet())
            .packageId(longCount.incrementAndGet())
            .chapterId(longCount.incrementAndGet());
    }
}
