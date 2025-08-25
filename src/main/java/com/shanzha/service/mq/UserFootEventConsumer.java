package com.shanzha.service.mq;

import com.shanzha.domain.UserFoot;
import com.shanzha.service.ArticleUpdateNotifier;
import com.shanzha.utils.JsonUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "praise", consumerGroup = "article-praise-consumer", selectorExpression = "praise")
public class UserFootEventConsumer implements RocketMQListener<String> {

    private final ArticleUpdateNotifier notifier;

    public UserFootEventConsumer(ArticleUpdateNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void onMessage(String message) {
        UserFoot foot = JsonUtil.toObj(message, UserFoot.class);
        notifier.notifyCollectors(foot.getDocumentId(), "interact");
    }
}
