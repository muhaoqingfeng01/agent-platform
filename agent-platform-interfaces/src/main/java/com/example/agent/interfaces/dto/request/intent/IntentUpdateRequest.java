package com.example.agent.interfaces.dto.request.intent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "编辑意图请求（含ID）")
public class IntentUpdateRequest {
    @NotBlank(message = "意图ID不能为空")
    @Schema(description = "意图ID")
    private String id;
    @Schema(description = "意图名称")
    private String intentName;
    @Schema(description = "匹配模式列表")
    private List<String> patterns;
    @Schema(description = "示例列表")
    private List<String> examples;
    @Schema(description = "LLM 提示词")
    private String llmPrompt;
    @Schema(description = "必填参数列表")
    private List<Map<String, Object>> requiredParams;
    @Schema(description = "风险等级")
    private String riskLevel;
}
