package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工具注册表持久化对象 — 映射 t_tool_registry 表.
 *
 * <p>schema_json 和 auth_config 为 JSON 字符串，
 * 在 RepositoryImpl 中通过 ObjectMapper 与 Domain 层的 ToolSchema/AuthConfig 互转.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolRegistryPO {

    /** 自增主键 */
    private Long id;

    /** 所属租户 ID */
    private Long tenantId;

    /** 工具业务唯一标识 — 格式: tool_{snowflake_id} */
    private String toolId;

    /** 工具名称 */
    private String name;

    /** 工具描述 — 供 LLM 理解工具用途 */
    private String description;

    /** 工具类型 — MCP / HTTP / BUILTIN / CUSTOM */
    private String toolType;

    /** 工具 Schema JSON — 包含 inputSchema 和 outputSchema */
    private String schemaJson;

    /** 连接端点 — MCP SSE URL 或 HTTP API 地址 */
    private String endpoint;

    /** 认证配置 JSON — 存储 API Key、Token 等认证信息 */
    private String authConfig;

    /** 是否需要审批 — 1 表示高风险工具需审批，0 表示无需审批 */
    private Boolean requireApproval;

    /** 工具状态 — ACTIVE（启用）/ DISABLED（禁用） */
    private String status;

    /** 当前版本号 — 每次配置变更自动+1 */
    private Integer version;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;

    /** 软删除标记 — 1 表示已删除 */
    private Boolean deleted;
}
