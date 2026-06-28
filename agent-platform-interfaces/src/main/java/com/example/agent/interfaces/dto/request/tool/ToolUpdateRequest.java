package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "更新工具请求（含ID）")
public class ToolUpdateRequest {
    @NotBlank(message = "工具ID不能为空")
    @Schema(description = "工具ID")
    private String id;
    @Schema(description = "工具名称")
    private String name;
    @Schema(description = "工具描述")
    private String description;
    @Schema(description = "工具类型")
    private String toolType;
    @Schema(description = "输入Schema")
    private Map<String, Object> inputSchema;
    @Schema(description = "输出Schema")
    private Map<String, Object> outputSchema;
    @Schema(description = "端点地址")
    private String endpoint;
    @Schema(description = "认证类型")
    private String authType;
    @Schema(description = "API Key")
    private String apiKey;
    @Schema(description = "Token")
    private String token;
    @Schema(description = "是否需要审批")
    private boolean requireApproval;
}
