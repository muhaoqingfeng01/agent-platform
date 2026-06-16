package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新工具请求 DTO — HTTP 接口层入参，全可选字段.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class UpdateToolRequest {

    @Schema(description = "工具名称")
    private String name;

    @Schema(description = "功能描述")
    private String description;

    @Schema(description = "工具类型", allowableValues = {"MCP", "HTTP", "BUILTIN", "CUSTOM"})
    private String toolType;

    @Schema(description = "输入参数 JSON Schema")
    private String inputSchema;

    @Schema(description = "输出格式 JSON Schema")
    private String outputSchema;

    @Schema(description = "连接端点")
    private String endpoint;

    @Schema(description = "认证类型")
    private String authType;

    @Schema(description = "API 密钥")
    private String apiKey;

    @Schema(description = "Bearer Token")
    private String token;

    @Schema(description = "是否需要审批")
    private Boolean requireApproval;
}
