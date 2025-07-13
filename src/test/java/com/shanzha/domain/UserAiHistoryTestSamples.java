package com.shanzha.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserAiHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserAiHistory getUserAiHistorySample1() {
        return new UserAiHistory().id(1L).userId(1L).question("question1").answer("answer1").chatId("chatId1");
    }

    public static UserAiHistory getUserAiHistorySample2() {
        return new UserAiHistory().id(2L).userId(2L).question("question2").answer("answer2").chatId("chatId2");
    }

    public static UserAiHistory getUserAiHistoryRandomSampleGenerator() {
        return new UserAiHistory()
            .id(longCount.incrementAndGet())
            .userId(longCount.incrementAndGet())
            .question(UUID.randomUUID().toString())
            .answer(UUID.randomUUID().toString())
            .chatId(UUID.randomUUID().toString());
    }
}
