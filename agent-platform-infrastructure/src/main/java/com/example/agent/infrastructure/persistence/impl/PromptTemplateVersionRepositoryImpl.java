package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.prompt.entity.PromptTemplateVersion;
import com.example.agent.domain.prompt.repository.PromptTemplateVersionRepository;
import com.example.agent.domain.prompt.valueobject.VariableDef;
import com.example.agent.infrastructure.persistence.mapper.PromptTemplateVersionMapper;
import com.example.agent.infrastructure.persistence.po.PromptTemplateVersionPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 提示词版本历史仓储 MyBatis 实现 — Memento 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class PromptTemplateVersionRepositoryImpl implements PromptTemplateVersionRepository {

    private final PromptTemplateVersionMapper versionMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void save(PromptTemplateVersion version) {
        versionMapper.insert(toPO(version));
    }

    @Override
    public Optional<PromptTemplateVersion> findByPromptIdAndVersion(String promptId, int version) {
        return versionMapper.selectByPromptIdAndVersion(promptId, version).map(this::toDomain);
    }

    @Override
    public List<PromptTemplateVersion> findByPromptId(String promptId) {
        return versionMapper.selectByPromptId(promptId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public int countByPromptId(String promptId) {
        return versionMapper.countByPromptId(promptId);
    }

    @Override
    public Optional<Integer> findMaxVersionByPromptId(String promptId) {
        return versionMapper.selectMaxVersionByPromptId(promptId);
    }

    // ==================== 映射方法 ====================

    private PromptTemplateVersion toDomain(PromptTemplateVersionPO po) {
        return PromptTemplateVersion.builder()
                .id(po.getId())
                .promptId(po.getPromptId())
                .version(po.getVersion())
                .templateText(po.getTemplateText())
                .variables(parseVariables(po.getVariablesJson()))
                .changeLog(po.getChangeLog())
                .publisher(po.getPublisher())
                .publishedAt(po.getPublishedAt())
                .createdAt(po.getCreatedAt())
                .build();
    }

    private PromptTemplateVersionPO toPO(PromptTemplateVersion version) {
        return PromptTemplateVersionPO.builder()
                .promptId(version.getPromptId())
                .version(version.getVersion())
                .templateText(version.getTemplateText())
                .variablesJson(toVariablesJson(version.getVariables()))
                .changeLog(version.getChangeLog())
                .publisher(version.getPublisher())
                .publishedAt(version.getPublishedAt())
                .createdAt(version.getCreatedAt())
                .build();
    }

    private List<VariableDef> parseVariables(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<VariableDef>>() {});
        } catch (JsonProcessingException e) {
            log.warn("[PromptVersion] 变量 JSON 解析失败: {}", json, e);
            return Collections.emptyList();
        }
    }

    private String toVariablesJson(List<VariableDef> variables) {
        if (variables == null || variables.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(variables);
        } catch (JsonProcessingException e) {
            log.warn("[PromptVersion] 变量 JSON 序列化失败", e);
            return "[]";
        }
    }
}
