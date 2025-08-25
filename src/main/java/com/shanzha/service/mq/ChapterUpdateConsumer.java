package com.shanzha.service.mq;

import com.shanzha.domain.ChapterUpdate;
import com.shanzha.service.ArticleUpdateNotifier;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "chapter_update", consumerGroup = "chapter-update-consumer")
public class ChapterUpdateConsumer implements RocketMQListener<ChapterUpdate> {

    private final ArticleUpdateNotifier notifier;

    public ChapterUpdateConsumer(ArticleUpdateNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void onMessage(ChapterUpdate update) {
        notifier.notifyCollectors(update.getNovelId(), "update");
    }
}
