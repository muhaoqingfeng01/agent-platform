package com.example.agent.interfaces.dto.request.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "版本差异对比请求")
public class PromptDiffVersionRequest {
    @NotBlank(message = "模板ID不能为空")
    @Schema(description = "模板ID")
    private String id;
    @Schema(description = "版本1")
    private int v1;
    @Schema(description = "版本2")
    private int v2;
}
