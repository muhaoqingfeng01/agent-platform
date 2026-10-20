package com.example.agent.infrastructure.checkpoint;

import com.example.agent.domain.conversation.ConversationCheckpointStore;
import com.example.agent.domain.conversation.valueobject.AgentPhase;
import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Redis List 检查点存储 — RPUSH 追加最新，LTRIM 保留最近 N 条.
 * <p>
 * Key：{@code ckpt:{tenantId}:{conversationId}}。TTL 与会话记忆对齐（SessionConfig）。
 * 读写失败 fail-open：不影响主对话，仅打 warn。
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisConversationCheckpointStore implements ConversationCheckpointStore {

    static final String KEY_PREFIX = "ckpt:";
    private static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final SessionConfig sessionConfig;

    @Override
    public void save(Long tenantId, String conversationId, ConversationCheckpoint checkpoint) {
        if (checkpoint == null || conversationId == null || conversationId.isBlank()) {
            return;
        }
        String key = buildKey(tenantId, conversationId);
        try {
            String json = objectMapper.writeValueAsString(CheckpointRedisEntry.from(checkpoint));
            stringRedisTemplate.opsForList().rightPush(key, json);
            long maxCount = sessionConfig.getCheckpointMaxCount();
            // RPUSH 后最新在右侧：LTRIM -N -1 保留最近 N 条（LTRIM 0 N-1 会裁掉最新）
            stringRedisTemplate.opsForList().trim(key, -maxCount, -1);
            stringRedisTemplate.expire(key, ttl());
            log.debug("[Checkpoint] 保存: key={}, state={}, step={}",
                    key, checkpoint.stateCode(), checkpoint.getStepIndex());
        } catch (Exception e) {
            log.warn("[Checkpoint] 保存失败（忽略，不影响主流程）: convId={}", conversationId, e);
        }
    }

    @Override
    public Optional<ConversationCheckpoint> loadLatest(Long tenantId, String conversationId) {
        List<ConversationCheckpoint> all = loadAll(tenantId, conversationId);
        if (all.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(all.get(all.size() - 1));
    }

    @Override
    public List<ConversationCheckpoint> loadAll(Long tenantId, String conversationId) {
        String key = buildKey(tenantId, conversationId);
        try {
            List<String> entries = stringRedisTemplate.opsForList().range(key, 0, -1);
            if (entries == null || entries.isEmpty()) {
                return List.of();
            }
            List<ConversationCheckpoint> result = new ArrayList<>();
            for (String json : entries) {
                Optional<ConversationCheckpoint> parsed = deserialize(json);
                if (parsed.isPresent()) {
                    result.add(parsed.get());
                } else {
                    log.warn("[Checkpoint] 反序列化失败，跳过该条: convId={}", conversationId);
                }
            }
            return result;
        } catch (Exception e) {
            log.warn("[Checkpoint] 读取失败，按 INIT 处理: convId={}", conversationId, e);
            return List.of();
        }
    }

    @Override
    public void clear(Long tenantId, String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            return;
        }
        try {
            stringRedisTemplate.delete(buildKey(tenantId, conversationId));
            log.debug("[Checkpoint] 清理: convId={}", conversationId);
        } catch (Exception e) {
            log.warn("[Checkpoint] 清理失败: convId={}", conversationId, e);
        }
    }

    private Optional<ConversationCheckpoint> deserialize(String json) {
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            CheckpointRedisEntry entry = objectMapper.readValue(json, CheckpointRedisEntry.class);
            return Optional.ofNullable(entry).map(CheckpointRedisEntry::toDomain);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            log.warn("[Checkpoint] 反序列化失败", e);
            return Optional.empty();
        }
    }

    private Duration ttl() {
        return Duration.ofMinutes(sessionConfig.getCheckpointTtlMinutes());
    }

    static String buildKey(Long tenantId, String conversationId) {
        long tenant = tenantId != null ? tenantId : 0L;
        return KEY_PREFIX + tenant + ":" + conversationId;
    }

    @Data
    @NoArgsConstructor
    static class CheckpointRedisEntry {
        private String checkpointId;
        private String conversationId;
        private String state;
        private String intentCode;
        private String slotsJson;
        private String executionId;
        private Integer stepIndex;
        private String createdAt;

        static CheckpointRedisEntry from(ConversationCheckpoint checkpoint) {
            CheckpointRedisEntry entry = new CheckpointRedisEntry();
            entry.checkpointId = checkpoint.getCheckpointId();
            entry.conversationId = checkpoint.getConversationId();
            entry.state = checkpoint.stateCode();
            entry.intentCode = checkpoint.getIntentCode();
            entry.slotsJson = checkpoint.getSlotsJson();
            entry.executionId = checkpoint.getExecutionId();
            entry.stepIndex = checkpoint.getStepIndex();
            LocalDateTime created = checkpoint.getCreatedAt() != null
                    ? checkpoint.getCreatedAt() : LocalDateTime.now();
            entry.createdAt = created.format(ISO_TIME);
            return entry;
        }

        ConversationCheckpoint toDomain() {
            AgentPhase phase;
            try {
                phase = AgentPhase.fromCode(state);
            } catch (IllegalArgumentException e) {
                throw e;
            }
            LocalDateTime created = parseTime(createdAt);
            return ConversationCheckpoint.builder()
                    .checkpointId(checkpointId)
                    .conversationId(conversationId)
                    .state(phase)
                    .intentCode(intentCode)
                    .slotsJson(slotsJson)
                    .executionId(executionId)
                    .stepIndex(stepIndex)
                    .createdAt(created)
                    .build();
        }

        private static LocalDateTime parseTime(String raw) {
            if (raw == null || raw.isBlank()) {
                return LocalDateTime.now();
            }
            try {
                return LocalDateTime.parse(raw, ISO_TIME);
            } catch (Exception e) {
                return LocalDateTime.now();
            }
        }
    }
}
