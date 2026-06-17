package com.example.agent.domain.tool.repository;

import com.example.agent.domain.tool.entity.ToolRegistryVersion;

import java.util.List;
import java.util.Optional;

/**
 * 工具版本历史仓储接口 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
public interface ToolRegistryVersionRepository {

    /** 保存版本快照 */
    void save(ToolRegistryVersion version);

    /** 按工具 ID + 版本号查询 */
    Optional<ToolRegistryVersion> findByToolIdAndVersion(String toolId, int version);

    /** 按工具 ID 查询所有版本历史 */
    List<ToolRegistryVersion> findByToolId(String toolId);

    /** 查询工具的最大版本号 */
    Optional<Integer> findMaxVersionByToolId(String toolId);

    /** 统计工具的版本数量 */
    int countByToolId(String toolId);
}
