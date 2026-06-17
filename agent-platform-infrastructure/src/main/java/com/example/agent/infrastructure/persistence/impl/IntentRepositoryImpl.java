package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.conversation.entity.Intent;
import com.example.agent.domain.conversation.repository.IntentRepository;
import com.example.agent.domain.conversation.valueobject.IntentStatus;
import com.example.agent.infrastructure.persistence.mapper.IntentMapper;
import com.example.agent.infrastructure.persistence.po.IntentPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 意图仓储 MyBatis 实现 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class IntentRepositoryImpl implements IntentRepository {

    private final IntentMapper intentMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void save(Intent intent) {
        intentMapper.insert(toPO(intent));
    }

    @Override
    public void update(Intent intent) {
        intentMapper.update(toPO(intent));
    }

    @Override
    public void updateStatus(String intentId, IntentStatus status) {
        intentMapper.updateStatus(intentId, status.name());
    }

    @Override
    public void softDelete(String intentId) {
        intentMapper.softDelete(intentId);
    }

    @Override
    public Optional<Intent> findByIntentId(String intentId) {
        return intentMapper.selectByIntentId(intentId).map(this::toDomain);
    }

    @Override
    public List<Intent> findActiveByTenant(Long tenantId) {
        return intentMapper.selectActiveByTenant(tenantId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Intent> findByTenant(Long tenantId, int page, int size) {
        return intentMapper.selectByTenant(tenantId, page * size, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByTenant(Long tenantId) {
        return intentMapper.countByTenant(tenantId);
    }

    @Override
    public boolean existsByTenantAndCode(Long tenantId, String intentCode) {
        return intentMapper.existsByTenantAndCode(tenantId, intentCode);
    }

    // ==================== 映射方法 ====================

    private Intent toDomain(IntentPO po) {
        return Intent.builder()
                .intentId(po.getIntentId())
                .tenantId(po.getTenantId())
                .intentCode(po.getIntentCode())
                .intentName(po.getIntentName())
                .category(po.getCategory())
                .patterns(parseJsonList(po.getPatterns()))
                .examples(parseJsonList(po.getExamples()))
                .llmPrompt(po.getLlmPrompt())
                .requiredParams(parseJsonMapList(po.getRequiredParams()))
                .riskLevel(po.getRiskLevel())
                .status(IntentStatus.valueOf(po.getStatus()))
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private IntentPO toPO(Intent intent) {
        return IntentPO.builder()
                .intentId(intent.getIntentId())
                .tenantId(intent.getTenantId())
                .intentCode(intent.getIntentCode())
                .intentName(intent.getIntentName())
                .category(intent.getCategory())
                .patterns(toJson(intent.getPatterns()))
                .examples(toJson(intent.getExamples()))
                .llmPrompt(intent.getLlmPrompt())
                .requiredParams(toJson(intent.getRequiredParams()))
                .riskLevel(intent.getRiskLevel())
                .status(intent.getStatus().name())
                .createdAt(intent.getCreatedAt() != null ? intent.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("[Intent] JSON 解析失败", e);
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> parseJsonMapList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            log.warn("[Intent] JSON 解析失败", e);
            return Collections.emptyList();
        }
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("[Intent] JSON 序列化失败", e);
            return "[]";
        }
    }
}
