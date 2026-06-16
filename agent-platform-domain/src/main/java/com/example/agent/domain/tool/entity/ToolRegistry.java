package com.example.agent.domain.tool.entity;

import com.example.agent.domain.tool.valueobject.AuthConfig;
import com.example.agent.domain.tool.valueobject.ToolSchema;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 工具注册聚合根 — 平台中每个可被 Agent 调用的工具的唯一注册记录.
 *
 * <p>工具是 Agent 的行动能力单元。每个工具由一个唯一 toolId 标识，
 * 包含供 LLM 理解的 Schema 描述、连接端点、认证配置和安全控制开关.
 * 工具按 toolType 分为 MCP（标准协议）、HTTP（REST 包装）、
 * BUILTIN（内置实现）、CUSTOM（自定义扩展）四种.
 *
 * <p>领域不变量：
 * <ul>
 *   <li>MCP 类型工具必须提供 endpoint</li>
 *   <li>同一租户下 toolId 唯一</li>
 *   <li>只有 ACTIVE 状态的工具可被 Agent 调用</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class ToolRegistry {

    /** 工具业务唯一标识 — 格式: tool_{snowflake_id}，如 tool_1896573294812348416 */
    private String toolId;

    /** 所属租户 ID — 多租户隔离，每个租户拥有独立的工具注册表 */
    private String tenantId;

    /** 工具名称 — 简洁描述工具功能，如 "订单查询"、"发送邮件" */
    private String name;

    /** 功能描述 — 详细说明工具的用途和能力边界，供 LLM 理解何时调用此工具 */
    private String description;

    /** 工具类型 — MCP / HTTP / BUILTIN / CUSTOM */
    private ToolType toolType;

    /** 工具 Schema — 输入参数和输出格式的 JSON Schema 定义 */
    private ToolSchema schema;

    /** 连接端点 — MCP SSE 端点 URL 或 HTTP API 地址 */
    private String endpoint;

    /** 认证配置 — 调用工具时所需的认证信息（API Key、Token 等） */
    private AuthConfig authConfig;

    /** 是否需要审批 — true 表示高风险工具，调用前需走审批流程（T11） */
    private boolean requireApproval;

    /** 工具状态 — ACTIVE（可调用）或 DISABLED（不可调用） */
    private ToolStatus status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;

    // ==================== 领域行为方法 ====================

    /**
     * 启用工具 — 将状态切换为 ACTIVE，使其可被 Agent 发现和调用.
     *
     * @throws IllegalStateException 如果工具已经是启用状态
     */
    public void enable() {
        if (this.status == ToolStatus.ACTIVE) {
            throw new IllegalStateException("工具已是启用状态: " + this.toolId);
        }
        this.status = ToolStatus.ACTIVE;
    }

    /**
     * 禁用工具 — 将状态切换为 DISABLED，使其不可被调用但保留配置.
     *
     * @throws IllegalStateException 如果工具已经是禁用状态
     */
    public void disable() {
        if (this.status == ToolStatus.DISABLED) {
            throw new IllegalStateException("工具已是禁用状态: " + this.toolId);
        }
        this.status = ToolStatus.DISABLED;
    }

    /**
     * 判断工具是否处于可调用状态.
     *
     * @return true 表示工具已启用，可被 Agent 调用
     */
    public boolean isActive() {
        return this.status == ToolStatus.ACTIVE;
    }

    /**
     * 判断工具是否已被禁用.
     *
     * @return true 表示工具已禁用，不可被调用
     */
    public boolean isDisabled() {
        return this.status == ToolStatus.DISABLED;
    }
}
