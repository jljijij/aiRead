package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChapterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Chapter getChapterSample1() {
        return new Chapter().id(1L).chapterNo(1).title("title1").contentId(1L).wordCount(1).price(1);
    }

    public static Chapter getChapterSample2() {
        return new Chapter().id(2L).chapterNo(2).title("title2").contentId(2L).wordCount(2).price(2);
    }

    public static Chapter getChapterRandomSampleGenerator() {
        return new Chapter()
            .id(longCount.incrementAndGet())
            .chapterNo(intCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .contentId(longCount.incrementAndGet())
            .wordCount(intCount.incrementAndGet())
            .price(intCount.incrementAndGet());
    }
}
