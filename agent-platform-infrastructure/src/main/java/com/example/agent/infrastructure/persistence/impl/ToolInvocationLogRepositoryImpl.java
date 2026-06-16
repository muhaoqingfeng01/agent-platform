package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tool.entity.ToolInvocationLog;
import com.example.agent.domain.tool.repository.ToolInvocationLogRepository;
import com.example.agent.domain.tool.valueobject.InvocationStatus;
import com.example.agent.infrastructure.persistence.mapper.ToolInvocationLogMapper;
import com.example.agent.infrastructure.persistence.po.ToolInvocationLogPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工具调用日志仓储 MyBatis 实现.
 *
 * <p>调用日志仅追加不更新，映射逻辑简单（inputJson/outputJson 直接存储为字符串）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ToolInvocationLogRepositoryImpl implements ToolInvocationLogRepository {

    /** MyBatis Mapper 代理接口 */
    private final ToolInvocationLogMapper mapper;

    // ==================== 写操作 ====================

    @Override
    public void save(ToolInvocationLog log) {
        mapper.insert(toPO(log));
    }

    // ==================== 读操作 ====================

    @Override
    public List<ToolInvocationLog> findByToolId(String toolId, int page, int size) {
        int offset = page * size;
        return mapper.selectByToolId(toolId, offset, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<ToolInvocationLog> findByTenant(String tenantId, int page, int size) {
        int offset = page * size;
        return mapper.selectByTenant(tenantId, offset, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public long countByToolId(String toolId) {
        return mapper.countByToolId(toolId);
    }

    @Override
    public long countByTenant(String tenantId) {
        return mapper.countByTenant(tenantId);
    }

    // ==================== 映射方法 ====================

    /**
     * 将持久化对象转换为领域实体.
     *
     * @param po 持久化对象
     * @return 领域实体
     */
    private ToolInvocationLog toDomain(ToolInvocationLogPO po) {
        return ToolInvocationLog.builder()
                .invocationId(po.getInvocationId())
                .tenantId(po.getTenantId())
                .toolId(po.getToolId())
                .conversationId(po.getConversationId())
                .messageId(po.getMessageId())
                .executionId(po.getExecutionId())
                .inputJson(po.getInputJson())
                .outputJson(po.getOutputJson())
                .status(InvocationStatus.fromCode(po.getStatus()))
                .durationMs(po.getDurationMs())
                .errorMessage(po.getErrorMessage())
                .createdAt(po.getCreatedAt())
                .build();
    }

    /**
     * 将领域实体转换为持久化对象.
     *
     * @param log 领域实体
     * @return 持久化对象
     */
    private ToolInvocationLogPO toPO(ToolInvocationLog log) {
        return ToolInvocationLogPO.builder()
                .invocationId(log.getInvocationId())
                .tenantId(log.getTenantId())
                .toolId(log.getToolId())
                .conversationId(log.getConversationId())
                .messageId(log.getMessageId())
                .executionId(log.getExecutionId())
                .inputJson(log.getInputJson())
                .outputJson(log.getOutputJson())
                .status(log.getStatus().name())
                .durationMs(log.getDurationMs())
                .errorMessage(log.getErrorMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
