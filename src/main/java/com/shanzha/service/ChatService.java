package com.shanzha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plexpt.chatgpt.listener.AbstractStreamListener;
import com.shanzha.domain.constants.ChatConstants;
import com.shanzha.domain.enumeration.AiChatStatEnum;
import com.shanzha.domain.enumeration.ChatAnswerTypeEnum;
import com.shanzha.security.SecurityUtils;
import com.shanzha.service.dto.ChatItemVo;
import com.shanzha.service.dto.ChatRecordsVo;
import com.shanzha.service.dto.ChatSessionItemVo;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SensitiveService sensitiveService;

    private final SimpMessagingTemplate messagingTemplate;

    UserAiHistoryService userAiHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    StringRedisTemplate redisTemplate;

    ChatHistoryService chatHistoryService;

    DeepSeekIntegration deepSeekIntegration;

    @Value("${ai.maxNum.historyContextCnt:10}")
    protected Integer chatHistoryContextNum;

    RedisClient redisClient;

    public void handleChat(String question) {
        autoChat(question, this::response);
    }

    public void autoChat(String question, Consumer<ChatRecordsVo> callback) {
        asyncChat(question, callback);
    }

    public void response(ChatRecordsVo response) {
        String username = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new IllegalStateException("当前未登录用户无法发送 WebSocket 消息"));
        messagingTemplate.convertAndSendToUser(username, "/chat/rsp", response);
    }

    public ChatRecordsVo asyncChat(String question, Consumer<ChatRecordsVo> consumer) {
        ChatRecordsVo res = initResVo(question);
        Long user = SecurityUtils.getCurrentUserId().get();
        if (!res.hasQaCnt()) {
            // 次数使用完毕
            consumer.accept(res);
            return res;
        }

        List<String> sensitiveWord = sensitiveService.contains(res.getRecords().get(0).getQuestion());
        if (!CollectionUtils.isEmpty(sensitiveWord)) {
            res.getRecords().get(0).initAnswer(String.format(ChatConstants.SENSITIVE_QUESTION, sensitiveWord));
            consumer.accept(res);
        } else {
            final ChatRecordsVo newRes = res.clone();
            AiChatStatEnum needReturn = doAsyncAnswer(user, newRes, (ans, vo) -> {
                if (ans == AiChatStatEnum.END) {
                    // 只有最后一个会话，即ai的回答结束，才需要进行持久化，并计数
                    processAfterSuccessedAnswered(user, newRes);
                } else if (ans == AiChatStatEnum.ERROR) {}
                // ai异步返回结果之后，我们将结果推送给前端用户
                consumer.accept(newRes);
            });

            if (needReturn.needResponse()) {
                // 异步响应时，为了避免长时间的等待，这里直接响应用户的提问，返回一个稍等得提示文案
                ChatItemVo nowItem = res.getRecords().get(0);
                nowItem.initAnswer(ChatConstants.ASYNC_CHAT_TIP);
                consumer.accept(res);
            }
        }
        return res;
    }

    protected void recordChatItem(Long user, ChatItemVo item) {
        // 保存聊天记录
        chatHistoryService.saveRecord(user, "", item);
    }

    private boolean containsSensitive(ChatRecordsVo record, Long userId) {
        String question = record.getRecords().get(0).getQuestion();
        List<String> badWords = sensitiveService.contains(question);
        if (!CollectionUtils.isEmpty(badWords)) {
            record.getRecords().get(0).initAnswer("问题中包含敏感词：" + badWords);
            return true;
        }
        return false;
    }

    private ChatRecordsVo initResVo(String question) {
        ChatRecordsVo res = new ChatRecordsVo();
        Long user = SecurityUtils.getCurrentUserId().get();
        int maxCnt = getMaxQaCnt(user);
        int usedCnt = queryUserdCnt(user);
        res.setMaxCnt(maxCnt);
        res.setUsedCnt(usedCnt);

        ChatItemVo item = new ChatItemVo().initQuestion(question);
        if (!res.hasQaCnt()) {
            // 次数已经使用完毕，不需要再与AI进行交互了；直接返回
            item.initAnswer(ChatConstants.TOKEN_OVER);
            res.setRecords(List.of(item));
            return res;
        }

        // 构建多轮对话的聊天上下文
        List<ChatItemVo> history = buildChatContext(user);
        history.add(0, item);
        res.setRecords(history);
        return res;
    }

    private int queryUserdCnt(Long user) {
        Integer cnt = RedisClient.hGet(ChatConstants.getAiRateKeyPerDay(), String.valueOf(user), Integer.class);
        if (cnt == null) {
            cnt = 0;
        }
        return cnt;
    }

    private List<ChatItemVo> buildChatContext(Long user) {
        // 用于多轮对话，我们这里只取最近的十条作为上下文传参
        List<ChatItemVo> history = chatHistoryService.listHistory(user, "", chatHistoryContextNum);
        if (CollectionUtils.isEmpty(history) || history.size() == 1) {
            return history;
        }

        // 过滤掉提示词之前的消息
        Iterator<ChatItemVo> iterator = history.iterator();
        boolean toRemove = false;
        while (iterator.hasNext()) {
            ChatItemVo tmp = iterator.next();
            if (!toRemove) {
                if (tmp.getQuestion().startsWith(ChatConstants.PROMPT_TAG)) {
                    // 找到提示词，之后的全部删除
                    toRemove = true;
                }
            } else {
                iterator.remove();
            }
        }
        return history;
    }

    protected int getMaxQaCnt(Long user) {
        return userAiHistoryService.getMaxChatCnt(user);
    }

    private void sendToUser(String username, String sessionId, ChatRecordsVo vo) {
        messagingTemplate.convertAndSendToUser(username, "/queue/chat/" + sessionId, vo);
    }

    public AiChatStatEnum doAsyncAnswer(Long user, ChatRecordsVo response, BiConsumer<AiChatStatEnum, ChatRecordsVo> consumer) {
        // 获取问答中的最新的记录，用于问答
        ChatItemVo item = response.getRecords().get(0);
        // 创建一个抽象流监听器来处理流式返回的结果
        AbstractStreamListener listener = new AbstractStreamListener() {
            // 当连接打开时的处理
            @Override
            public void onOpen(EventSource event, Response response) {
                super.onOpen(event, response);
                if (log.isDebugEnabled()) {
                    log.debug("正确建立了连接: {}, res: {}", event, response);
                }
            }

            // 当连接关闭时的处理
            @Override
            public void onClosed(EventSource eventSource) {
                super.onClosed(eventSource);
                if (log.isDebugEnabled()) {
                    log.debug("已经关闭了连接: {}", eventSource);
                }
                // 检查是否正常结束对话
                if (item.getAnswerType() != ChatAnswerTypeEnum.STREAM_END) {
                    // 主动结束这一次的对话
                    if (StringUtils.isBlank(lastMessage)) {
                        item
                            .appendAnswer("大模型超时未返回结果，主动关闭会话；请重新提问吧\n")
                            .setAnswerType(ChatAnswerTypeEnum.STREAM_END);
                        consumer.accept(AiChatStatEnum.ERROR, response);
                    } else {
                        item.appendAnswer("\n").setAnswerType(ChatAnswerTypeEnum.STREAM_END);
                        consumer.accept(AiChatStatEnum.END, response);
                    }
                }
            }

            // 当接收到消息时的处理
            @Override
            public void onMsg(String message) {
                // 成功返回结果的场景, 过滤掉开头的空行
                if (StringUtils.isNotBlank(lastMessage)) {
                    item.appendAnswer(message);
                    consumer.accept(AiChatStatEnum.MID, response);
                    if (log.isDebugEnabled()) {
                        log.debug("DeepSeek返回内容: {}", lastMessage);
                    }
                }
            }

            // 当遇到错误时的处理
            @Override
            public void onError(Throwable throwable, String res) {
                // 返回异常的场景
                item
                    .appendAnswer("Error:" + (StringUtils.isBlank(res) ? throwable.getMessage() : res))
                    .setAnswerType(ChatAnswerTypeEnum.STREAM_END);
                consumer.accept(AiChatStatEnum.ERROR, response);
                if (log.isDebugEnabled()) {
                    log.debug("DeepSeek返回异常: {}", lastMessage);
                }
            }
        };

        // 注册回答结束的回调钩子
        listener.setOnComplate(s -> {
            if (log.isDebugEnabled()) {
                log.debug("这一轮对话聊天已结束，完整的返回结果是：{}", s);
            }
            item.appendAnswer("\n").setAnswerType(ChatAnswerTypeEnum.STREAM_END);
            consumer.accept(AiChatStatEnum.END, response);
        });
        // 调用深度寻求流式返回的方法
        deepSeekIntegration.streamReturn(response.getRecords(), listener);
        return AiChatStatEnum.IGNORE;
    }

    private Long getUserId(String username) {
        // 可集成用户服务或缓存系统
        return 10001L; // mock
    }

    private String getChatIdKey(Long userId, String chatId) {
        return StringUtils.isBlank(chatId)
            ? ChatConstants.getAiHistoryRecordsKey(userId)
            : ChatConstants.getAiHistoryRecordsKey(userId + ":" + chatId);
    }

    public void saveRecord(Long userId, String chatId, ChatItemVo item) {
        // 写入 MySQL
        chatHistoryService.pushChatItem(userId, item);

        // Redis key 构造
        String key = getChatIdKey(userId, chatId);
        String sessionKey = ChatConstants.getAiChatListKey(userId);

        try {
            // 将 item 序列化为 JSON 并 lpush 到记录列表中
            String itemJson = objectMapper.writeValueAsString(item);
            redisTemplate.execute(
                (RedisCallback<Long>) conn -> conn.lPush(key.getBytes(StandardCharsets.UTF_8), itemJson.getBytes(StandardCharsets.UTF_8))
            );

            // 获取会话元数据
            byte[] sessionBytes = redisTemplate.execute(
                (RedisCallback<byte[]>) conn ->
                    conn.hGet(sessionKey.getBytes(StandardCharsets.UTF_8), chatId.getBytes(StandardCharsets.UTF_8))
            );

            ChatSessionItemVo session;
            if (sessionBytes == null) {
                // 不存在则新建会话
                session = new ChatSessionItemVo();
                session.setChatId(chatId);
                session.setTitle(
                    !item.getQuestion().startsWith(ChatConstants.PROMPT_TAG)
                        ? item.getQuestion()
                        : item.getQuestion().substring(ChatConstants.PROMPT_TAG.length())
                );
                session.setCreatTime(System.currentTimeMillis());
                session.setUpdateTime(session.getCreatTime());
                session.setQasCnt(1);
            } else {
                // 反序列化旧会话
                session = objectMapper.readValue(new String(sessionBytes, StandardCharsets.UTF_8), ChatSessionItemVo.class);
                session.setUpdateTime(System.currentTimeMillis());
                session.setQasCnt(session.getQasCnt() + 1);
            }

            // 写入更新后的会话信息
            String sessionJson = objectMapper.writeValueAsString(session);
            redisTemplate.execute(
                (RedisCallback<Boolean>) conn ->
                    conn.hSet(
                        sessionKey.getBytes(StandardCharsets.UTF_8),
                        chatId.getBytes(StandardCharsets.UTF_8),
                        sessionJson.getBytes(StandardCharsets.UTF_8)
                    )
            );

            // 限制记录长度（注意 zset/ltrim 的 start,end 含义）
            if (session.getQasCnt() > ChatConstants.MAX_HISTORY_RECORD_ITEMS) {
                redisTemplate.execute(
                    (RedisCallback<Void>) conn -> {
                        conn.lTrim(key.getBytes(StandardCharsets.UTF_8), 0, ChatConstants.MAX_HISTORY_RECORD_ITEMS);
                        return null;
                    }
                );
            }
        } catch (Exception e) {
            // 记录异常
            log.error("Failed to save chat record to Redis", e);
        }
    }

    protected void processAfterSuccessedAnswered(Long user, ChatRecordsVo response) {
        // 回答成功，保存聊天记录，剩余次数-1
        response.setUsedCnt(incrCnt(user).intValue());
        recordChatItem(user, response.getRecords().get(0));
    }

    protected Long incrCnt(Long user) {
        String key = ChatConstants.getAiRateKeyPerDay();
        String field = String.valueOf(user);
        Long cnt = redisTemplate.execute(
            (RedisCallback<Long>) connection ->
                connection.hIncrBy(key.getBytes(StandardCharsets.UTF_8), field.getBytes(StandardCharsets.UTF_8), 1)
        );

        if (cnt != null && cnt == 1L) {
            redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.expire(key.getBytes(StandardCharsets.UTF_8), 86400L));
            return cnt;
        }
        return cnt;
    }
}
