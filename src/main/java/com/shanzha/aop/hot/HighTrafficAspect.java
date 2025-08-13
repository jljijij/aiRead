package com.shanzha.aop.hot;



import com.shanzha.service.ChapterContentService;
import com.shanzha.service.ChapterTrafficMonitor;
import com.shanzha.service.NovelRankService;
import com.shanzha.service.dto.RedisContent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Aspect
@Component
public class HighTrafficAspect {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ChapterContentService chapterContentService;
    @Autowired
    ChapterTrafficMonitor chapterTrafficMonitor;

    @Autowired
    NovelRankService novelRankService;
    private static final long THRESHOLD = 100; // 阈值，每秒100次请求
    private static final long WINDOW_SECONDS = 1000; // 统计时间窗口(秒)
    private static final int corePoolSize = 5;       // 核心线程数
    private static final int maxPoolSize = 10;       // 最大线程数
    private static final long keepAliveTime = 60L;   // 空闲线程存活时间(秒)
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100); // 工作队列

    private static final ExecutorService workpool = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            workQueue,
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
    );
    private static final int BATCH_SIZE = 4;

    // 定义切入点：所有Controller中的public方法
    @Pointcut("execution(public * com.github.paicoding.forum.service.novel.service.ChunkUploadService.handleChunkUpload(..))")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object monitorTraffic(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();
        long novelId = (long)args[0];
        ChapterTrafficMonitor.ChapterHitResult chapterHitResult = chapterTrafficMonitor.reportReadEvent(novelId, WINDOW_SECONDS, THRESHOLD);
        boolean hot = chapterHitResult.isHot();

        if(hot)
        {
            loadNovel(novelId);
        }
        novelRankService.incrementReadCount(novelId);
        return joinPoint.proceed();
    }
    /*
    过期时间30分钟
     */
    public void loadNovel(Long novelId) {
        int pageSize = 5;
        int pageIndex = 0; // 页码从 0 开始
        Page<RedisContent> page;

        do {
            page = chapterContentService.getRedisContentsByNovelId(novelId, pageIndex, pageSize);

            if (!page.isEmpty()) {
                Page<RedisContent> finalPage = page;
                workpool.submit(() -> {
                    stringRedisTemplate.execute(new SessionCallback<Void>() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public @NotNull Void execute(@NotNull RedisOperations operations) throws DataAccessException {
                            operations.multi();  // 开启事务
                            for (RedisContent data : finalPage) {
                                String key = "novel:" + data.getNovelId() + "--" + data.getChapterId() + "--" + data.getPage();
                                String value = new String(data.getContent());
                                operations.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
                            }
                            operations.exec();  // 提交事务
                            return null;
                        }
                    });
                });
            }

            pageIndex++; // ✅ 分页推进

        } while (!page.isEmpty());
    }
}

