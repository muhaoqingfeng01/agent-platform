package com.example.agent.interfaces.dto.request.intent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "启停意图请求（含ID）")
public class IntentToggleStatusRequest {
    @NotBlank(message = "意图ID不能为空")
    @Schema(description = "意图ID")
    private String id;
    @NotBlank(message = "状态不能为空")
    @Schema(description = "目标状态")
    private String status;
}
