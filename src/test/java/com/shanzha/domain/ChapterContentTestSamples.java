package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChapterContentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ChapterContent getChapterContentSample1() {
        return new ChapterContent().id(1L).novelId(1L).pageId(1L).compressionType(1).hash("hash1");
    }

    public static ChapterContent getChapterContentSample2() {
        return new ChapterContent().id(2L).novelId(2L).pageId(2L).compressionType(2).hash("hash2");
    }

    public static ChapterContent getChapterContentRandomSampleGenerator() {
        return new ChapterContent()
            .id(longCount.incrementAndGet())
            .novelId(longCount.incrementAndGet())
            .pageId(longCount.incrementAndGet())
            .compressionType(intCount.incrementAndGet())
            .hash(UUID.randomUUID().toString());
    }
}
