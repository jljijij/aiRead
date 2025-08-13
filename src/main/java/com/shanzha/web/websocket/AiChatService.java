package com.shanzha.web.websocket;

import com.shanzha.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.security.Principal;

@Slf4j
public class AiChatService {

    ChatService chatService;
    @MessageMapping("/chat/{session}")
    public void chat(@Payload String msg,
                     @DestinationVariable("session") String sessionId,
                     Principal principal) {

        try {
            chatService.handleChat(msg);
        } catch (Exception e) {
            log.warn("处理消息失败: user={}, session={}, msg={}", principal.getName(), sessionId, msg, e);
            // 可以广播一个错误消息给用户
        }
    }
}
