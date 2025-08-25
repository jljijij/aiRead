package com.shanzha.service.mq;

import com.shanzha.domain.Comment;
import com.shanzha.service.ArticleUpdateNotifier;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "notify", consumerGroup = "article-comment-consumer", selectorExpression = "*")
public class CommentEventConsumer implements RocketMQListener<Comment> {

    private final ArticleUpdateNotifier notifier;

    public CommentEventConsumer(ArticleUpdateNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void onMessage(Comment comment) {
        notifier.notifyCollectors(comment.getArticleId(), "comment");
    }
}
