package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NovelTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Novel getNovelSample1() {
        return new Novel()
            .id(1L)
            .title("title1")
            .authorId(1L)
            .coverUrl("coverUrl1")
            .description("description1")
            .categoryId(1)
            .tags("tags1")
            .wordCount(1)
            .chapterCount(1)
            .status(1);
    }

    public static Novel getNovelSample2() {
        return new Novel()
            .id(2L)
            .title("title2")
            .authorId(2L)
            .coverUrl("coverUrl2")
            .description("description2")
            .categoryId(2)
            .tags("tags2")
            .wordCount(2)
            .chapterCount(2)
            .status(2);
    }

    public static Novel getNovelRandomSampleGenerator() {
        return new Novel()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .authorId(longCount.incrementAndGet())
            .coverUrl(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .categoryId(intCount.incrementAndGet())
            .tags(UUID.randomUUID().toString())
            .wordCount(intCount.incrementAndGet())
            .chapterCount(intCount.incrementAndGet())
            .status(intCount.incrementAndGet());
    }
}
