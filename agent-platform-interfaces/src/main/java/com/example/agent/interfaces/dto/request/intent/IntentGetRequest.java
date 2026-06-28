package com.example.agent.interfaces.dto.request.intent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "意图ID请求")
public class IntentGetRequest {
    @NotBlank(message = "意图ID不能为空")
    @Schema(description = "意图ID")
    private String id;
}
