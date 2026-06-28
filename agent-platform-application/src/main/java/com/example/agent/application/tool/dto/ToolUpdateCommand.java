package com.example.agent.application.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新工具命令 — 全可选字段，仅更新非 null 值.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class ToolUpdateCommand {

    /** 工具名称 */
    @Schema(description = "工具名称", example = "订单查询 V2")
    private String name;

    /** 功能描述 */
    @Schema(description = "功能描述")
    private String description;

    /** 工具类型 */
    @Schema(description = "工具类型", allowableValues = {"MCP", "HTTP", "BUILTIN", "CUSTOM"})
    private String toolType;

    /** 输入参数 JSON Schema */
    @Schema(description = "输入参数 JSON Schema")
    private String inputSchema;

    /** 输出格式 JSON Schema */
    @Schema(description = "输出格式 JSON Schema")
    private String outputSchema;

    /** 连接端点 */
    @Schema(description = "连接端点")
    private String endpoint;

    /** 认证类型 */
    @Schema(description = "认证类型", allowableValues = {"API_KEY", "BEARER", "BASIC", "NONE"})
    private String authType;

    /** API 密钥 */
    @Schema(description = "API 密钥")
    private String apiKey;

    /** Bearer Token */
    @Schema(description = "Bearer Token")
    private String token;

    /** 是否需要审批 */
    @Schema(description = "是否需要审批")
    private Boolean requireApproval;
}
