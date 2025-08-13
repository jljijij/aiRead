package com.shanzha.service;

import com.shanzha.domain.constants.ChatConstants;
import com.shanzha.service.dto.ChatItemVo;
import com.shanzha.service.dto.ChatSessionItemVo;

import com.shanzha.service.dto.UserAiHistoryDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.shanzha.utils.JsonUtil;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatHistoryService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserAiHistoryService userAiHistoryService;

    public List<ChatSessionItemVo> listChatSessions(Long userId) {
        String key = ChatConstants.getAiChatListKey(userId);
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);
        return raw.values().stream()
            .map(v -> JsonUtil.toObj((String) v, ChatSessionItemVo.class))
            .sorted(Comparator.comparing(ChatSessionItemVo::getUpdateTime).reversed())
            .collect(Collectors.toList());
    }

    public List<ChatItemVo> listHistory(Long userId, String chatId, Integer size) {
        size = size == null ? 50 : size;
        String key = getChatIdKey(userId, chatId);
        List<String> raw = redisTemplate.opsForList().range(key, 0, size);

        return raw == null ? new ArrayList<>() : raw.stream()
            .map(item -> JsonUtil.toObj(item, ChatItemVo.class))
            .collect(Collectors.toList());
    }

    public void pushChatItem(Long user, ChatItemVo item) {
        UserAiHistoryDTO userAiHistoryDTO = new UserAiHistoryDTO();
        userAiHistoryDTO.setUserId(user);
        userAiHistoryDTO.setQuestion(item.getQuestion());
        userAiHistoryDTO.setAnswer(item.getAnswer());
        userAiHistoryDTO.setChatId("");
        userAiHistoryService.save(userAiHistoryDTO);
    }

    public void saveRecord(Long userId, String chatId, ChatItemVo item) {
        // 存入数据库
        pushChatItem(userId, item);

        String key = getChatIdKey(userId, chatId);
        redisTemplate.opsForList().leftPush(key, JsonUtil.toStr(item));

        String sessionKey = ChatConstants.getAiChatListKey(userId);
        String sessionStr = (String) redisTemplate.opsForHash().get(sessionKey, chatId);
        ChatSessionItemVo session = sessionStr == null ? null : JsonUtil.toObj(sessionStr, ChatSessionItemVo.class);

        if (session == null) {
            session = new ChatSessionItemVo();
            session.setChatId(chatId);
            session.setTitle(item.getQuestion().startsWith(ChatConstants.PROMPT_TAG)
                ? item.getQuestion().substring(ChatConstants.PROMPT_TAG.length())
                : item.getQuestion());
            session.setCreatTime(System.currentTimeMillis());
            session.setUpdateTime(session.getCreatTime());
            session.setQasCnt(1);
        } else {
            session.setUpdateTime(System.currentTimeMillis());
            session.setQasCnt(session.getQasCnt() + 1);
        }

        redisTemplate.opsForHash().put(sessionKey, chatId, JsonUtil.toStr(session));

        if (session.getQasCnt() > ChatConstants.MAX_HISTORY_RECORD_ITEMS) {
            redisTemplate.opsForList().trim(key, 0, ChatConstants.MAX_HISTORY_RECORD_ITEMS);
        }
    }

    public Boolean updateChatSessionName(String chatId, String title, Long userId) {
        String key = ChatConstants.getAiChatListKey(userId);
        String sessionStr = (String) redisTemplate.opsForHash().get(key, chatId);
        ChatSessionItemVo item = sessionStr == null ? null : JsonUtil.toObj(sessionStr, ChatSessionItemVo.class);

        if (item != null && !Objects.equals(item.getTitle(), title)) {
            item.setTitle(title);
            redisTemplate.opsForHash().put(key, chatId, JsonUtil.toStr(item));
            return true;
        }
        return true;
    }

    public Boolean removeChatSession(String chatId, Long userId) {
        String key = ChatConstants.getAiChatListKey(userId);
        redisTemplate.opsForHash().delete(key, chatId);
        return true;
    }

    private String getChatIdKey(Long userId, String chatId) {
        return StringUtils.isBlank(chatId)
            ? ChatConstants.getAiHistoryRecordsKey(userId)
            : ChatConstants.getAiHistoryRecordsKey(userId + ":" + chatId);
    }
}
