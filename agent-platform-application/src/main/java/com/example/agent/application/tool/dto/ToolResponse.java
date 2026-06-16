package com.example.agent.application.tool.dto;

import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.valueobject.AuthConfig;
import com.example.agent.domain.tool.valueobject.ToolSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工具响应 DTO — 用于 API 返回工具详情和列表.
 *
 * <p>包含工具全量信息及前端展示用的中文标签（toolTypeLabel / statusLabel）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class ToolResponse {

    /** 工具业务唯一标识 */
    @Schema(description = "工具 ID")
    private String toolId;

    /** 所属租户 ID */
    @Schema(description = "租户 ID")
    private String tenantId;

    /** 工具名称 */
    @Schema(description = "工具名称")
    private String name;

    /** 功能描述（供 LLM 理解） */
    @Schema(description = "功能描述")
    private String description;

    /** 工具类型代码 — MCP / HTTP / BUILTIN / CUSTOM */
    @Schema(description = "工具类型代码")
    private String toolType;

    /** 工具类型中文标签 */
    @Schema(description = "工具类型中文", example = "HTTP接口")
    private String toolTypeLabel;

    /** 工具 Schema — 输入参数和输出格式定义 */
    @Schema(description = "工具 Schema")
    private ToolSchema schema;

    /** 连接端点 */
    @Schema(description = "连接端点")
    private String endpoint;

    /** 认证配置 */
    @Schema(description = "认证配置")
    private AuthConfig authConfig;

    /** 是否需要审批 */
    @Schema(description = "是否需要审批")
    private boolean requireApproval;

    /** 状态代码 — ACTIVE / DISABLED */
    @Schema(description = "状态代码")
    private String status;

    /** 状态中文标签 — 启用 / 禁用 */
    @Schema(description = "状态中文")
    private String statusLabel;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 从领域实体构建响应 DTO — 工厂方法.
     *
     * <p>自动将枚举类型转换为代码字符串和中文标签，
     * 前端可直接展示 toolTypeLabel 和 statusLabel.
     *
     * @param entity 工具注册领域实体
     * @return 工具响应 DTO
     */
    public static ToolResponse from(ToolRegistry entity) {
        ToolResponse r = new ToolResponse();
        r.setToolId(entity.getToolId());
        r.setTenantId(entity.getTenantId());
        r.setName(entity.getName());
        r.setDescription(entity.getDescription());
        r.setToolType(entity.getToolType() != null ? entity.getToolType().name() : null);
        r.setToolTypeLabel(entity.getToolType() != null ? entity.getToolType().toChinese() : null);
        r.setSchema(entity.getSchema());
        r.setEndpoint(entity.getEndpoint());
        r.setAuthConfig(entity.getAuthConfig());
        r.setRequireApproval(entity.isRequireApproval());
        r.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        r.setStatusLabel(entity.getStatus() != null ? entity.getStatus().toChinese() : null);
        r.setCreatedAt(entity.getCreatedAt());
        r.setUpdatedAt(entity.getUpdatedAt());
        return r;
    }
}
