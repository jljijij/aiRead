package com.shanzha.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ChapterPermissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChapterPermission getChapterPermissionSample1() {
        return new ChapterPermission().id(1L).userId(1L).chapterId(1L);
    }

    public static ChapterPermission getChapterPermissionSample2() {
        return new ChapterPermission().id(2L).userId(2L).chapterId(2L);
    }

    public static ChapterPermission getChapterPermissionRandomSampleGenerator() {
        return new ChapterPermission()
            .id(longCount.incrementAndGet())
            .userId(longCount.incrementAndGet())
            .chapterId(longCount.incrementAndGet());
    }
}
