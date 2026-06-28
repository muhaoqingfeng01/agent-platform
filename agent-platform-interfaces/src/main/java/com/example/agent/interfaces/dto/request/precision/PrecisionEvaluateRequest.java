package com.example.agent.interfaces.dto.request.precision;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "精度评估请求")
public class PrecisionEvaluateRequest {
    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String kbId;
    @NotBlank(message = "评测数据集ID不能为空")
    @Schema(description = "评测数据集ID")
    private String datasetId;
}
