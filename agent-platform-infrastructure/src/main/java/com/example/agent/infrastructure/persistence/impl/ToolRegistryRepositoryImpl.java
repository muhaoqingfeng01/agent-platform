package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.domain.tool.valueobject.AuthConfig;
import com.example.agent.domain.tool.valueobject.ToolSchema;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;
import com.example.agent.infrastructure.persistence.mapper.ToolRegistryMapper;
import com.example.agent.infrastructure.persistence.po.ToolRegistryPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工具注册仓储 MyBatis 实现.
 *
 * <p>负责 PO ↔ Domain 的双向映射，处理 schema_json 和 auth_config
 * 两个 JSON 列与领域值对象 ToolSchema / AuthConfig 之间的序列化与反序列化.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ToolRegistryRepositoryImpl implements ToolRegistryRepository {

    /** JSON 序列化/反序列化工具 — 静态常量避免重复创建 */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** MyBatis Mapper 代理接口 */
    private final ToolRegistryMapper mapper;

    // ==================== 写操作 ====================

    @Override
    public void save(ToolRegistry tool) {
        mapper.insert(toPO(tool));
    }

    @Override
    public void update(ToolRegistry tool) {
        mapper.update(toPO(tool));
    }

    @Override
    public void updateStatus(String toolId, ToolStatus status) {
        mapper.updateStatus(toolId, status.name());
    }

    @Override
    public void softDelete(String toolId) {
        mapper.softDelete(toolId);
    }

    // ==================== 读操作 ====================

    @Override
    public Optional<ToolRegistry> findByToolId(String toolId) {
        return mapper.selectByToolId(toolId).map(this::toDomain);
    }

    @Override
    public List<ToolRegistry> findByTenant(String tenantId, int page, int size) {
        int offset = page * size;
        return mapper.selectByTenant(tenantId, offset, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public long countByTenant(String tenantId) {
        return mapper.countByTenant(tenantId);
    }

    @Override
    public List<ToolRegistry> findByTenantAndType(String tenantId, ToolType toolType, int page, int size) {
        int offset = page * size;
        return mapper.selectByTenantAndType(tenantId, toolType.name(), offset, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public long countByTenantAndType(String tenantId, ToolType toolType) {
        return mapper.countByTenantAndType(tenantId, toolType.name());
    }

    @Override
    public List<ToolRegistry> findByTypeAndStatus(ToolType toolType, ToolStatus status) {
        return mapper.selectByTypeAndStatus(toolType.name(), status.name()).stream()
                .map(this::toDomain).toList();
    }

    // ==================== 映射方法 ====================

    /**
     * 将持久化对象转换为领域实体.
     * <p>schema_json 和 auth_config JSON 字符串反序列化为对应的值对象.
     *
     * @param po 持久化对象
     * @return 领域实体
     */
    private ToolRegistry toDomain(ToolRegistryPO po) {
        return ToolRegistry.builder()
                .toolId(po.getToolId())
                .tenantId(po.getTenantId())
                .name(po.getName())
                .description(po.getDescription())
                .toolType(ToolType.fromCode(po.getToolType()))
                .schema(parseSchema(po.getSchemaJson()))
                .endpoint(po.getEndpoint())
                .authConfig(parseAuthConfig(po.getAuthConfig()))
                .requireApproval(po.getRequireApproval() != null && po.getRequireApproval())
                .status(ToolStatus.fromCode(po.getStatus()))
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    /**
     * 将领域实体转换为持久化对象.
     * <p>ToolSchema 和 AuthConfig 值对象序列化为 JSON 字符串.
     *
     * @param tool 领域实体
     * @return 持久化对象
     */
    private ToolRegistryPO toPO(ToolRegistry tool) {
        return ToolRegistryPO.builder()
                .tenantId(tool.getTenantId())
                .toolId(tool.getToolId())
                .name(tool.getName())
                .description(tool.getDescription())
                .toolType(tool.getToolType().name())
                .schemaJson(toJsonString(tool.getSchema()))
                .endpoint(tool.getEndpoint())
                .authConfig(toJsonString(tool.getAuthConfig()))
                .requireApproval(tool.isRequireApproval())
                .status(tool.getStatus().name())
                .createdAt(tool.getCreatedAt())
                .updatedAt(tool.getUpdatedAt())
                .build();
    }

    // ==================== JSON 工具方法 ====================

    /**
     * 将 JSON 字符串反序列化为 ToolSchema 值对象.
     *
     * @param schemaJson JSON 字符串（可能为 null）
     * @return ToolSchema 值对象，null 输入返回 null
     */
    private ToolSchema parseSchema(String schemaJson) {
        if (schemaJson == null || schemaJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(schemaJson, ToolSchema.class);
        } catch (JsonProcessingException e) {
            log.error("[Tool] 解析 schema_json 失败: toolId 数据异常", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串反序列化为 AuthConfig 值对象.
     *
     * @param authConfigJson JSON 字符串（可能为 null）
     * @return AuthConfig 值对象，null 输入返回 null
     */
    private AuthConfig parseAuthConfig(String authConfigJson) {
        if (authConfigJson == null || authConfigJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(authConfigJson, AuthConfig.class);
        } catch (JsonProcessingException e) {
            log.error("[Tool] 解析 auth_config 失败", e);
            return null;
        }
    }

    /**
     * 将对象序列化为 JSON 字符串.
     *
     * @param obj 待序列化对象（可能为 null）
     * @return JSON 字符串，null 输入返回 null
     */
    private String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[Tool] JSON 序列化失败: class={}", obj.getClass().getName(), e);
            return null;
        }
    }
}
