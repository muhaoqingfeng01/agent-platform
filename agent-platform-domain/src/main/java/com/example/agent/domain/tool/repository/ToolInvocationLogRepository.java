package com.example.agent.domain.tool.repository;

import com.example.agent.domain.tool.entity.ToolInvocationLog;

import java.util.List;

/**
 * 工具调用日志仓储接口 — 定义调用日志的持久化契约.
 *
 * <p>调用日志是只追加的数据（Append-Only），不支持更新和删除.
 * 查询按工具 ID 或租户 ID 分页，用于调用历史查看和统计分析.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface ToolInvocationLogRepository {

    /**
     * 保存调用日志 — 仅追加，不更新.
     *
     * @param log 调用日志实体
     */
    void save(ToolInvocationLog log);

    /**
     * 按工具分页查询调用日志.
     *
     * @param toolId 工具业务 ID
     * @param page   页码（从 0 开始）
     * @param size   每页数量
     * @return 调用日志列表（按创建时间倒序）
     */
    List<ToolInvocationLog> findByToolId(String toolId, int page, int size);

    /**
     * 按租户分页查询所有调用日志.
     *
     * @param tenantId 租户 ID
     * @param page     页码（从 0 开始）
     * @param size     每页数量
     * @return 调用日志列表（按创建时间倒序）
     */
    List<ToolInvocationLog> findByTenant(String tenantId, int page, int size);

    /**
     * 统计指定工具的调用次数.
     *
     * @param toolId 工具业务 ID
     * @return 调用总次数
     */
    long countByToolId(String toolId);

    /**
     * 统计租户下所有工具的调用次数.
     *
     * @param tenantId 租户 ID
     * @return 调用总次数
     */
    long countByTenant(String tenantId);
}
