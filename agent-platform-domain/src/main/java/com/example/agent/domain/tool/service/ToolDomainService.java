package com.example.agent.domain.tool.service;

import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 工具领域服务 — 封装跨实体的工具业务规则.
 *
 * <p>负责校验工具注册的不变量、租户访问权限和状态转换合法性.
 * 所有断言方法在校验失败时抛出异常，调用方无需处理返回值.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class ToolDomainService {

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
    public void assertTenantAccess(ToolRegistry tool, String currentTenantId) {
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
}
