package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.prompt.entity.PromptTemplate;
import com.example.agent.domain.prompt.repository.PromptTemplateRepository;
import com.example.agent.domain.prompt.valueobject.PromptStatus;
import com.example.agent.domain.prompt.valueobject.VariableDef;
import com.example.agent.infrastructure.persistence.mapper.PromptTemplateMapper;
import com.example.agent.infrastructure.persistence.po.PromptTemplatePO;
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
import java.util.Optional;

/**
 * 提示词模板仓储 MyBatis 实现 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class PromptTemplateRepositoryImpl implements PromptTemplateRepository {

    private final PromptTemplateMapper promptTemplateMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void save(PromptTemplate template) {
        promptTemplateMapper.insert(toPO(template));
    }

    @Override
    public Optional<PromptTemplate> findByPromptId(String promptId) {
        return promptTemplateMapper.selectByPromptId(promptId).map(this::toDomain);
    }

    @Override
    public List<PromptTemplate> findByTenantId(Long tenantId, int offset, int size) {
        return promptTemplateMapper.selectByTenantId(tenantId, offset, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByTenantId(Long tenantId) {
        return promptTemplateMapper.countByTenantId(tenantId);
    }

    @Override
    public List<PromptTemplate> findByTenantIdAndStatus(Long tenantId, PromptStatus status,
                                                         int offset, int size) {
        return promptTemplateMapper.selectByTenantIdAndStatus(tenantId, status.name(), offset, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<PromptTemplate> findByTenantIdAndName(Long tenantId, String name) {
        return promptTemplateMapper.selectByTenantIdAndName(tenantId, name).map(this::toDomain);
    }

    @Override
    public void updateContent(String promptId, String name, String description,
                              String templateText, String variablesJson) {
        promptTemplateMapper.updateContent(promptId, name, description, templateText, variablesJson);
    }

    @Override
    public void publish(String promptId, int newVersion, String templateText, String variablesJson) {
        promptTemplateMapper.publish(promptId, newVersion, templateText, variablesJson);
    }

    @Override
    public void updateStatus(String promptId, PromptStatus status) {
        promptTemplateMapper.updateStatus(promptId, status.name());
    }

    @Override
    public void softDelete(String promptId) {
        promptTemplateMapper.softDelete(promptId);
    }

    @Override
    public Optional<PromptTemplate> findLatestPublished(Long tenantId, String name) {
        return promptTemplateMapper.selectLatestPublished(tenantId, name).map(this::toDomain);
    }

    @Override
    public Optional<PromptTemplate> findByVersion(Long tenantId, String name, int version) {
        return promptTemplateMapper.selectByVersion(tenantId, name, version).map(this::toDomain);
    }

    // ==================== 映射方法 ====================

    private PromptTemplate toDomain(PromptTemplatePO po) {
        return PromptTemplate.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .promptId(po.getPromptId())
                .name(po.getName())
                .description(po.getDescription())
                .templateText(po.getTemplateText())
                .variables(parseVariables(po.getVariablesJson()))
                .version(po.getVersion())
                .status(PromptStatus.valueOf(po.getStatus()))
                .abTestConfig(po.getAbTestConfig())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .deleted(po.getDeleted())
                .build();
    }

    private PromptTemplatePO toPO(PromptTemplate template) {
        return PromptTemplatePO.builder()
                .tenantId(template.getTenantId())
                .promptId(template.getPromptId())
                .name(template.getName())
                .description(template.getDescription())
                .templateText(template.getTemplateText())
                .variablesJson(toVariablesJson(template.getVariables()))
                .version(template.getVersion() != null ? template.getVersion() : 1)
                .status(template.getStatus() != null ? template.getStatus().name() : "DRAFT")
                .abTestConfig(template.getAbTestConfig())
                .createdAt(template.getCreatedAt() != null ? template.getCreatedAt() : LocalDateTime.now())
                .updatedAt(template.getUpdatedAt() != null ? template.getUpdatedAt() : LocalDateTime.now())
                .build();
    }

    private List<VariableDef> parseVariables(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<VariableDef>>() {});
        } catch (JsonProcessingException e) {
            log.warn("[PromptTemplate] 变量 JSON 解析失败: {}", json, e);
            return Collections.emptyList();
        }
    }

    private String toVariablesJson(List<VariableDef> variables) {
        if (variables == null || variables.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(variables);
        } catch (JsonProcessingException e) {
            log.warn("[PromptTemplate] 变量 JSON 序列化失败", e);
            return "[]";
        }
    }
}
