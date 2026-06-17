package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.ToolInvocationLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工具调用日志 MyBatis Mapper — 对应 t_tool_invocation_log 表的数据库操作.
 *
 * <p>调用日志只追加不更新，仅提供 insert 和 select 方法.
 * 查询按创建时间倒序排列（最新的在前）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface ToolInvocationLogMapper {

    /**
     * 插入调用日志记录.
     *
     * @param po 调用日志持久化对象
     * @return 影响行数
     */
    int insert(ToolInvocationLogPO po);

    /**
     * 按工具分页查询调用日志 — 按创建时间倒序.
     *
     * @param toolId 工具业务 ID
     * @param offset 偏移量
     * @param size   每页数量
     * @return 调用日志列表
     */
    List<ToolInvocationLogPO> selectByToolId(@Param("toolId") String toolId,
                                              @Param("offset") int offset,
                                              @Param("size") int size);

    /**
     * 统计指定工具的调用次数.
     *
     * @param toolId 工具业务 ID
     * @return 调用总次数
     */
    long countByToolId(@Param("toolId") String toolId);

    /**
     * 按租户分页查询所有调用日志 — 按创建时间倒序.
     *
     * @param tenantId 租户 ID
     * @param offset   偏移量
     * @param size     每页数量
     * @return 调用日志列表
     */
    List<ToolInvocationLogPO> selectByTenant(@Param("tenantId") Long tenantId,
                                              @Param("offset") int offset,
                                              @Param("size") int size);

    /**
     * 统计租户下所有调用次数.
     *
     * @param tenantId 租户 ID
     * @return 调用总次数
     */
    long countByTenant(@Param("tenantId") Long tenantId);
}
