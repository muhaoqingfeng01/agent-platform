package com.example.agent.application.memory;

import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 短期记忆服务 — 管理 Redis 中最近 N 轮对话.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionMemoryService {

    private static final int MAX_ROUNDS = 20;
    private static final int MAX_MESSAGES = MAX_ROUNDS * 2;
    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;  // 使用 Spring Boot 自动配置的 ObjectMapper（已支持 Java 8 日期时间）

    public void appendMessage(String conversationId, Message message) {
        String key = buildSessionKey(conversationId);
        try {
            String json = objectMapper.writeValueAsString(MessageCacheEntry.from(message));
            redisTemplate.opsForList().rightPush(key, json);
            redisTemplate.opsForList().trim(key, -MAX_MESSAGES, -1);
            redisTemplate.expire(key, TTL);
        } catch (JsonProcessingException e) {
            log.warn("[SessionMemory] 序列化失败: msgId={}", message.getMessageId(), e);
        }
    }

    public List<Message> getRecentMessages(String conversationId, int rounds) {
        String key = buildSessionKey(conversationId);
        int count = Math.min(rounds * 2, MAX_MESSAGES);
        List<String> entries = redisTemplate.opsForList().range(key, -count, -1);
        if (entries == null || entries.isEmpty()) return List.of();
        return entries.stream()
                .map(this::deserialize)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Message> loadContextForLLM(String conversationId, int maxTokens) {
        List<Message> allMessages = getRecentMessages(conversationId, MAX_ROUNDS);
        List<Message> result = new ArrayList<>();
        int accumulatedTokens = 0;
        for (int i = allMessages.size() - 1; i >= 0; i--) {
            Message msg = allMessages.get(i);
            int msgTokens = msg.getTokenCount() != null ? msg.getTokenCount() : 0;
            if (accumulatedTokens + msgTokens > maxTokens) break;
            result.add(0, msg);
            accumulatedTokens += msgTokens;
        }
        return result;
    }

    public void touch(String conversationId) {
        redisTemplate.expire(buildSessionKey(conversationId), TTL);
    }

    public void clear(String conversationId) {
        redisTemplate.delete(buildSessionKey(conversationId));
    }

    private String buildSessionKey(String conversationId) {
        return "session:" + TenantContext.getCurrentTenantId() + ":" + conversationId;
    }

    private Message deserialize(String json) {
        try {
            MessageCacheEntry entry = objectMapper.readValue(json, MessageCacheEntry.class);
            return entry.toMessage();
        } catch (JsonProcessingException e) {
            log.warn("[SessionMemory] 反序列化失败", e);
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageCacheEntry {
        private String messageId;
        private String conversationId;
        private String role;
        private String content;
        private Integer tokenCount;
        private LocalDateTime createdAt;

        public static MessageCacheEntry from(Message msg) {
            return new MessageCacheEntry(
                    msg.getMessageId(), msg.getConversationId(), msg.getRole().name(),
                    msg.getContent(), msg.getTokenCount(), msg.getCreatedAt());
        }

        public Message toMessage() {
            return Message.builder()
                    .messageId(messageId).conversationId(conversationId)
                    .role(com.example.agent.domain.conversation.valueobject.MessageRole.valueOf(role))
                    .content(content).tokenCount(tokenCount).createdAt(createdAt)
                    .build();
        }
    }
}
