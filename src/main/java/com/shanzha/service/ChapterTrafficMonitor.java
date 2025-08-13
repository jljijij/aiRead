package com.shanzha.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ChapterTrafficMonitor {

    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local now = tonumber(ARGV[1])\n" +
                    "local windowSize = tonumber(ARGV[2])\n" +
                    "local threshold = tonumber(ARGV[3])\n" +
                    "local randomVal = ARGV[4]\n" +
                    "redis.call('ZREMRANGEBYSCORE', key, 0, now - windowSize)\n" +
                    "redis.call('ZADD', key, now, randomVal)\n" +
                    "redis.call('EXPIRE', key, windowSize/1000 + 60)\n" +
                    "local count = redis.call('ZCARD', key)\n" +
                    "if count >= threshold then\n" +
                    "    return {1, count}\n" +
                    "else\n" +
                    "    return {0, count}\n" +
                    "end";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<List> redisScript;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(List.class);
    }

    /**
     * 上报章节阅读事件
     * @param novelId 小说ID
     * @param windowSize 窗口大小(毫秒)
     * @param threshold 阈值
     * @return 是否触发热点(以及当前计数)
     */
    public ChapterHitResult reportReadEvent(long novelId, long windowSize, long threshold) {
        String key = "novel:hot:read:" + novelId;
        long now = System.currentTimeMillis();
        String randomVal = UUID.randomUUID().toString();

        List<Long> result = stringRedisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                String.valueOf(now),
                String.valueOf(windowSize),
                String.valueOf(threshold),
                randomVal
        );

        boolean isHot = result.get(0) == 1L;
        long currentCount = result.get(1);

        return new ChapterHitResult(isHot, currentCount);
    }

    @Data
    @AllArgsConstructor
    public static class ChapterHitResult {
        private boolean hot;      // 是否热点
        private long currentCount; // 当前窗口内访问量
    }
}
