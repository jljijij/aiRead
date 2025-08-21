package com.shanzha.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NovelRankService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String READ_COUNT_KEY = "novel:read:count";

    private static final String RANKING_KEY = "novel:ranking";

    /**
     * 增加小说阅读量
     */
    public void incrementReadCount(long novelId) {
        // 使用管道提高性能
        stringRedisTemplate.executePipelined(
            (RedisCallback<Object>) connection -> {
                // 1. HASH中增加阅读量
                connection.hashCommands().hIncrBy(READ_COUNT_KEY.getBytes(), String.valueOf(novelId).getBytes(), 1L);

                // 2. ZSET中更新排行榜
                Double currentScore = connection.zSetCommands().zScore(RANKING_KEY.getBytes(), String.valueOf(novelId).getBytes());

                connection
                    .zSetCommands()
                    .zAdd(RANKING_KEY.getBytes(), (currentScore != null ? currentScore : 0) + 1, String.valueOf(novelId).getBytes());

                return null;
            }
        );
    }

    /**
     * 获取小说阅读量
     */
    public long getReadCount(long novelId) {
        String count = (String) stringRedisTemplate.opsForHash().get(READ_COUNT_KEY, String.valueOf(novelId));
        return count != null ? Long.parseLong(count) : 0;
    }

    /**
     * 获取排行榜前N的小说ID
     */
    public List<Long> getTopNovels(int limit) {
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, limit - 1);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    /**
     * 删除排行榜和阅读量中的小说记录
     */
    public void removeFromRanking(long novelId) {
        stringRedisTemplate.opsForZSet().remove(RANKING_KEY, String.valueOf(novelId));
        stringRedisTemplate.opsForHash().delete(READ_COUNT_KEY, String.valueOf(novelId));
    }
}
