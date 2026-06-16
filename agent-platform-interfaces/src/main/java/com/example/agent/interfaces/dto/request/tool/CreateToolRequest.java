package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建工具请求 DTO — HTTP 接口层入参.
 *
 * <p>Controller 中通过 toAppRequest() 映射为 Application 层 CreateToolRequest.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class CreateToolRequest {

    /** 工具名称 */
    @NotBlank(message = "工具名称不能为空")
    @Schema(description = "工具名称", example = "订单查询")
    private String name;

    /** 功能描述 */
    @Schema(description = "功能描述（供 LLM 理解）", example = "查询用户的订单信息，支持按时间范围和状态筛选")
    private String description;

    /** 工具类型 — MCP / HTTP / BUILTIN / CUSTOM */
    @NotBlank(message = "工具类型不能为空")
    @Schema(description = "工具类型", example = "HTTP", allowableValues = {"MCP", "HTTP", "BUILTIN", "CUSTOM"})
    private String toolType;

    /** 输入参数 JSON Schema */
    @Schema(description = "输入参数 JSON Schema")
    private String inputSchema;

    /** 输出格式 JSON Schema */
    @Schema(description = "输出格式 JSON Schema")
    private String outputSchema;

    /** 连接端点 */
    @Schema(description = "MCP SSE 端点或 HTTP URL", example = "https://api.example.com/orders/query")
    private String endpoint;

    /** 认证类型 */
    @Schema(description = "认证类型", example = "API_KEY", allowableValues = {"API_KEY", "BEARER", "BASIC", "NONE"})
    private String authType;

    /** API 密钥 */
    @Schema(description = "API 密钥")
    private String apiKey;

    /** Bearer Token */
    @Schema(description = "Bearer Token")
    private String token;

    /** 是否需要审批 */
    @Schema(description = "高风险操作需审批", example = "false")
    private boolean requireApproval;
}
