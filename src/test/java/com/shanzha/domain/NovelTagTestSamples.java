package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NovelTagTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static NovelTag getNovelTagSample1() {
        return new NovelTag().id(1L).tagId("tagId1").tagName("tagName1").category("category1");
    }

    public static NovelTag getNovelTagSample2() {
        return new NovelTag().id(2L).tagId("tagId2").tagName("tagName2").category("category2");
    }

    public static NovelTag getNovelTagRandomSampleGenerator() {
        return new NovelTag()
            .id(longCount.incrementAndGet())
            .tagId(UUID.randomUUID().toString())
            .tagName(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString());
    }
}
