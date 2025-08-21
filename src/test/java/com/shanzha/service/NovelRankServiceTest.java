package com.shanzha.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.IntegrationTest;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

@IntegrationTest
class NovelRankServiceTest {

    @Autowired
    private NovelRankService novelRankService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String READ_COUNT_KEY = "novel:read:count";
    private static final String RANKING_KEY = "novel:ranking";

    @BeforeEach
    void cleanRedis() {
        stringRedisTemplate.delete(READ_COUNT_KEY);
        stringRedisTemplate.delete(RANKING_KEY);
    }

    @Test
    void shouldHandleConcurrentIncrement() throws InterruptedException {
        long novelId = 1L;
        int threadCount = 10;
        int iterations = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                for (int j = 0; j < iterations; j++) {
                    novelRankService.incrementReadCount(novelId);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        long expected = (long) threadCount * iterations;
        assertThat(novelRankService.getReadCount(novelId)).isEqualTo(expected);
        Double score = stringRedisTemplate.opsForZSet().score(RANKING_KEY, String.valueOf(novelId));
        assertThat(score).isEqualTo((double) expected);
        List<Long> top = novelRankService.getTopNovels(1);
        assertThat(top).containsExactly(novelId);
    }
}
