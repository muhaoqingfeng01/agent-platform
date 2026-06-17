package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.ToolRegistryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 工具注册 MyBatis Mapper — 对应 t_tool_registry 表的数据库操作.
 *
 * <p>所有 SQL 定义在 resources/mapper/ToolRegistryMapper.xml 中.
 * 查询使用 offset/limit 分页，offset = page * size.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface ToolRegistryMapper {

    /**
     * 插入新工具记录.
     *
     * @param po 工具持久化对象
     * @return 影响行数
     */
    int insert(ToolRegistryPO po);

    /**
     * 更新工具全部字段.
     *
     * @param po 工具持久化对象（含更新后字段）
     * @return 影响行数
     */
    int update(ToolRegistryPO po);

    /**
     * 更新工具并指定版本号 — 用于回滚操作.
     */
    int updateWithVersion(ToolRegistryPO po);

    /**
     * 更新工具状态 — 仅启停操作.
     *
     * @param toolId 工具业务 ID
     * @param status 目标状态
     * @return 影响行数
     */
    int updateStatus(@Param("toolId") String toolId, @Param("status") String status);

    /**
     * 软删除工具 — 设置 deleted = 1.
     *
     * @param toolId 工具业务 ID
     * @return 影响行数
     */
    int softDelete(@Param("toolId") String toolId);

    /**
     * 根据业务 ID 查询工具.
     *
     * @param toolId 工具业务唯一标识
     * @return 工具持久化对象，不存在返回 Optional.empty()
     */
    Optional<ToolRegistryPO> selectByToolId(@Param("toolId") String toolId);

    /**
     * 分页查询租户下的所有工具.
     *
     * @param tenantId 租户 ID
     * @param offset   偏移量 (page * size)
     * @param size     每页数量
     * @return 工具列表
     */
    List<ToolRegistryPO> selectByTenant(@Param("tenantId") String tenantId,
                                         @Param("offset") int offset,
                                         @Param("size") int size);

    /**
     * 统计租户下的工具总数.
     *
     * @param tenantId 租户 ID
     * @return 工具数量（不含已删除）
     */
    long countByTenant(@Param("tenantId") String tenantId);

    /**
     * 按工具类型筛选分页查询.
     *
     * @param tenantId 租户 ID
     * @param toolType 工具类型
     * @param offset   偏移量
     * @param size     每页数量
     * @return 符合条件的工具列表
     */
    List<ToolRegistryPO> selectByTenantAndType(@Param("tenantId") String tenantId,
                                                @Param("toolType") String toolType,
                                                @Param("offset") int offset,
                                                @Param("size") int size);

    /**
     * 按工具类型统计数量.
     *
     * @param tenantId 租户 ID
     * @param toolType 工具类型
     * @return 符合条件的工具数量
     */
    long countByTenantAndType(@Param("tenantId") String tenantId,
                              @Param("toolType") String toolType);

    /**
     * 查询指定类型和状态的所有工具 — 供 McpClientManager 使用.
     * <p>不按租户过滤，返回全平台符合条件的工具.
     *
     * @param toolType 工具类型
     * @param status   工具状态
     * @return 工具列表
     */
    List<ToolRegistryPO> selectByTypeAndStatus(@Param("toolType") String toolType,
                                                @Param("status") String status);
}
