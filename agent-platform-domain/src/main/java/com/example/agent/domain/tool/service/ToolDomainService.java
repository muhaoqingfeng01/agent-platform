package com.example.agent.domain.tool.service;

import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.entity.ToolRegistryVersion;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.domain.tool.repository.ToolRegistryVersionRepository;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 工具领域服务 — 封装跨实体的工具业务规则.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolDomainService {

    private final ToolRegistryRepository toolRepository;
    private final ToolRegistryVersionRepository versionRepository;

    /**
     * 校验新建工具的业务不变量.
     *
     * <p>规则：
     * <ul>
     *   <li>工具名称不可为空</li>
     *   <li>工具类型必须合法</li>
     *   <li>MCP 类型工具必须提供 endpoint（连接端点）</li>
     * </ul>
     *
     * @param tool 待校验的工具实体
     * @throws IllegalArgumentException 如果校验不通过
     */
    public void validateNewTool(ToolRegistry tool) {
        if (tool.getName() == null || tool.getName().isBlank()) {
            throw new IllegalArgumentException("工具名称不能为空");
        }

        if (tool.getToolType() == null) {
            throw new IllegalArgumentException("工具类型不能为空");
        }

        if (tool.getToolType() == ToolType.MCP) {
            if (tool.getEndpoint() == null || tool.getEndpoint().isBlank()) {
                throw new IllegalArgumentException("MCP 类型工具必须提供连接端点 (endpoint)");
            }
        }

        log.debug("[Tool] 工具校验通过: name={}, type={}", tool.getName(), tool.getToolType());
    }

    /**
     * 校验租户访问权限 — 确保当前租户只能操作自己的工具.
     *
     * @param tool             工具实体
     * @param currentTenantId  当前请求的租户 ID
     * @throws SecurityException 如果租户不匹配
     */
    public void assertTenantAccess(ToolRegistry tool, Long currentTenantId) {
        if (currentTenantId == null || !currentTenantId.equals(tool.getTenantId())) {
            throw new SecurityException(
                    "无权访问该工具: toolId=" + tool.getToolId()
                            + ", ownerTenant=" + tool.getTenantId()
                            + ", requestTenant=" + currentTenantId);
        }
    }

    /**
     * 校验工具是否可以从 DISABLED 切换为 ACTIVE.
     *
     * @param tool 工具实体
     * @throws IllegalStateException 如果工具当前不是 DISABLED 状态
     */
    public void assertCanEnable(ToolRegistry tool) {
        if (tool.getStatus() != ToolStatus.DISABLED) {
            throw new IllegalStateException(
                    "只有已禁用的工具才能启用，当前状态: " + tool.getStatus().toChinese()
                            + " (toolId=" + tool.getToolId() + ")");
        }
        log.debug("[Tool] 启用校验通过: toolId={}", tool.getToolId());
    }

    /**
     * 校验工具是否可以从 ACTIVE 切换为 DISABLED.
     *
     * @param tool 工具实体
     * @throws IllegalStateException 如果工具当前不是 ACTIVE 状态
     */
    public void assertCanDisable(ToolRegistry tool) {
        if (tool.getStatus() != ToolStatus.ACTIVE) {
            throw new IllegalStateException(
                    "只有已启用的工具才能禁用，当前状态: " + tool.getStatus().toChinese()
                            + " (toolId=" + tool.getToolId() + ")");
        }
        log.debug("[Tool] 禁用校验通过: toolId={}", tool.getToolId());
    }

    // ==================== 版本化管理 ====================

    /**
     * 更新工具配置并创建版本快照.
     *
     * @param tool         当前工具实体
     * @param changeReason 变更原因
     * @return 更新后的工具实体
     */
    public ToolRegistry updateWithVersion(ToolRegistry tool, String changeReason) {
        // 1. 快照当前版本 → t_tool_registry_version
        ToolRegistryVersion snapshot = ToolRegistryVersion.from(tool, changeReason);
        versionRepository.save(snapshot);

        // 2. 版本号 +1
        tool.incrementVersion();

        // 3. 持久化（含版本号更新）
        toolRepository.updateWithVersion(tool);

        log.info("[Tool] 工具配置更新（版本化）: toolId={}, newVersion={}, reason={}",
                tool.getToolId(), tool.getVersion(), changeReason);
        return tool;
    }

    /**
     * 回滚到指定历史版本.
     *
     * @param current       当前工具实体
     * @param targetVersion 目标版本号
     * @param operator      操作人
     * @return 更新后的工具实体
     */
    public ToolRegistry rollback(ToolRegistry current, int targetVersion, String operator) {
        // 1. 从版本历史读取目标版本
        ToolRegistryVersion target = versionRepository
                .findByToolIdAndVersion(current.getToolId(), targetVersion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "版本不存在: toolId=" + current.getToolId() + " v" + targetVersion));

        // 2. 快照当前版本（保留回滚前的状态）
        String preRollbackReason = "回滚到 v" + targetVersion + " 前的自动快照";
        ToolRegistryVersion preRollbackSnapshot = ToolRegistryVersion.from(current, preRollbackReason);
        versionRepository.save(preRollbackSnapshot);

        // 3. 应用目标版本的内容到当前实体
        current.incrementVersion();
        current.restoreFromVersion(target);

        // 4. 持久化
        toolRepository.updateWithVersion(current);

        log.info("[Tool] 工具回滚成功: toolId={}, {} → v{} (based on v{}), operator={}",
                current.getToolId(), current.getVersion(), current.getVersion(), targetVersion, operator);
        return current;
    }
}
