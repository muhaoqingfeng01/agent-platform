package com.example.agent.application.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建工具请求 DTO — 注册新工具时的入参.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class CreateToolRequest {

    /** 工具名称 — 简洁描述工具功能，如 "订单查询"、"发送邮件" */
    @NotBlank(message = "工具名称不能为空")
    @Schema(description = "工具名称", example = "订单查询")
    private String name;

    /** 功能描述 — 详细说明工具用途，供 LLM 理解何时调用 */
    @Schema(description = "功能描述（供 LLM 理解）", example = "查询用户的订单信息，支持按时间范围和状态筛选")
    private String description;

    /** 工具类型 — MCP / HTTP / BUILTIN / CUSTOM */
    @NotBlank(message = "工具类型不能为空")
    @Schema(description = "工具类型", example = "HTTP", allowableValues = {"MCP", "HTTP", "BUILTIN", "CUSTOM"})
    private String toolType;

    /** 输入参数 JSON Schema — 定义调用时需传入的参数结构 */
    @Schema(description = "输入参数 JSON Schema", example = "{\"type\":\"object\",\"properties\":{\"status\":{\"type\":\"string\"}}}")
    private String inputSchema;

    /** 输出格式 JSON Schema — 定义返回结果的数据结构 */
    @Schema(description = "输出格式 JSON Schema", example = "{\"type\":\"array\",\"items\":{\"type\":\"object\"}}")
    private String outputSchema;

    /** 连接端点 — MCP SSE URL 或 HTTP API 地址 */
    @Schema(description = "MCP SSE 端点或 HTTP URL", example = "https://api.example.com/orders/query")
    private String endpoint;

    /** 认证类型 — API_KEY / BEARER / BASIC / NONE */
    @Schema(description = "认证类型", example = "API_KEY", allowableValues = {"API_KEY", "BEARER", "BASIC", "NONE"})
    private String authType;

    /** API 密钥 — authType=API_KEY 时必填 */
    @Schema(description = "API 密钥")
    private String apiKey;

    /** Bearer Token — authType=BEARER 时必填 */
    @Schema(description = "Bearer Token")
    private String token;

    /** 是否需要审批 — true 表示高风险工具需走审批流（T11） */
    @Schema(description = "高风险操作需审批", example = "false")
    private boolean requireApproval;
}
