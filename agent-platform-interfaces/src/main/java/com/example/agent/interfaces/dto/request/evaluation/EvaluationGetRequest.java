package com.example.agent.interfaces.dto.request.evaluation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "评测ID请求")
public class EvaluationGetRequest {
    @NotBlank(message = "评测ID不能为空")
    @Schema(description = "评测ID")
    private String evaluationId;
}
