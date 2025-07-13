package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CommentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Comment getCommentSample1() {
        return new Comment().id(1L).articleId(1L).userId(1L).content("content1").parentCommentId(1L).topCommentId(1L).deleted(1);
    }

    public static Comment getCommentSample2() {
        return new Comment().id(2L).articleId(2L).userId(2L).content("content2").parentCommentId(2L).topCommentId(2L).deleted(2);
    }

    public static Comment getCommentRandomSampleGenerator() {
        return new Comment()
            .id(longCount.incrementAndGet())
            .articleId(longCount.incrementAndGet())
            .userId(longCount.incrementAndGet())
            .content(UUID.randomUUID().toString())
            .parentCommentId(longCount.incrementAndGet())
            .topCommentId(longCount.incrementAndGet())
            .deleted(intCount.incrementAndGet());
    }
}
