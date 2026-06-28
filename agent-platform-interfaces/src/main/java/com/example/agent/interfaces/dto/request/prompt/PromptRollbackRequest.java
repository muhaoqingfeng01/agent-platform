package com.example.agent.interfaces.dto.request.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "回滚版本请求")
public class PromptRollbackRequest {
    @NotBlank(message = "模板ID不能为空")
    @Schema(description = "模板ID")
    private String id;
    @Schema(description = "目标版本号")
    private int version;
}
