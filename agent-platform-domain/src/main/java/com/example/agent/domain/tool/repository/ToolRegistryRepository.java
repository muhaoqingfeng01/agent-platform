package com.example.agent.domain.tool.repository;

import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;

import java.util.List;
import java.util.Optional;

/**
 * 工具注册仓储接口 — 定义工具注册实体的持久化契约.
 *
 * <p>所有查询按租户隔离，列表查询支持分页和按类型筛选.
 * 更新操作采用细粒度方法，每个方法对应一个特定的字段变更场景.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface ToolRegistryRepository {

    /**
     * 保存新工具.
     *
     * @param tool 工具注册实体
     */
    void save(ToolRegistry tool);

    /**
     * 根据业务 ID 查询工具.
     *
     * @param toolId 工具业务唯一标识
     * @return 工具实体，不存在返回 Optional.empty()
     */
    Optional<ToolRegistry> findByToolId(String toolId);

    /**
     * 分页查询租户下的所有工具.
     *
     * @param tenantId 租户 ID
     * @param page     页码（从 0 开始）
     * @param size     每页数量
     * @return 工具列表
     */
    List<ToolRegistry> findByTenant(Long tenantId, int page, int size);

    /**
     * 统计租户下的工具总数.
     *
     * @param tenantId 租户 ID
     * @return 工具数量
     */
    long countByTenant(Long tenantId);

    /**
     * 按工具类型筛选分页查询.
     *
     * @param tenantId 租户 ID
     * @param toolType 工具类型
     * @param page     页码（从 0 开始）
     * @param size     每页数量
     * @return 符合条件的工具列表
     */
    List<ToolRegistry> findByTenantAndType(Long tenantId, ToolType toolType, int page, int size);

    /**
     * 按工具类型统计数量.
     *
     * @param tenantId 租户 ID
     * @param toolType 工具类型
     * @return 符合条件的工具数量
     */
    long countByTenantAndType(Long tenantId, ToolType toolType);

    /**
     * 查询指定类型和状态的所有工具 — 供 McpClientManager 定时刷新使用.
     * <p>不限制租户，返回全平台所有符合条件的工具（用于建立 MCP 连接）.
     *
     * @param toolType 工具类型
     * @param status   工具状态
     * @return 工具列表
     */
    List<ToolRegistry> findByTypeAndStatus(ToolType toolType, ToolStatus status);

    /**
     * 更新工具全部字段.
     *
     * @param tool 工具实体（含更新后的字段值）
     */
    void update(ToolRegistry tool);

    /**
     * 更新工具并指定版本号 — 用于回滚操作时精确控制版本.
     *
     * @param tool 工具实体（含目标版本号）
     */
    void updateWithVersion(ToolRegistry tool);

    /**
     * 更新工具状态 — 用于启停操作.
     *
     * @param toolId 工具业务 ID
     * @param status 目标状态
     */
    void updateStatus(String toolId, ToolStatus status);

    /**
     * 软删除工具 — 将 deleted 标记设为 true.
     *
     * @param toolId 工具业务 ID
     */
    void softDelete(String toolId);
}
