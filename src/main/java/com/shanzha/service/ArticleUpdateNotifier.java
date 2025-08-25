package com.shanzha.service;

import com.shanzha.domain.enumeration.DocumentTypeEnum;
import com.shanzha.repository.UserFootRepository;
import com.shanzha.service.dto.ArticleNotifyMessage;
import java.util.List;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
public class ArticleUpdateNotifier {

    private final UserFootRepository userFootRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    public ArticleUpdateNotifier(UserFootRepository userFootRepository, SimpMessageSendingOperations messagingTemplate) {
        this.userFootRepository = userFootRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyCollectors(Long articleId, String type) {
        if (articleId == null) {
            return;
        }
        List<Long> userIds = userFootRepository.findCollectorUserIds(articleId, DocumentTypeEnum.ARTICLE.getCode());
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        ArticleNotifyMessage payload = new ArticleNotifyMessage(articleId, type);
        userIds.forEach(uid -> messagingTemplate.convertAndSendToUser(uid.toString(), "/topic/article-updates", payload));
    }
}
