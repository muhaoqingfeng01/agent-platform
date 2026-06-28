package com.example.agent.interfaces.dto.request.evaluation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "数据集ID请求")
public class DatasetGetRequest {
    @NotBlank(message = "数据集ID不能为空")
    @Schema(description = "数据集ID")
    private String datasetId;
}
