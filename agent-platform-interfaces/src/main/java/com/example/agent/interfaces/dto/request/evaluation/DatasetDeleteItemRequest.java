package com.example.agent.interfaces.dto.request.evaluation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "删除样本请求")
public class DatasetDeleteItemRequest {
    @NotBlank(message = "数据集ID不能为空")
    @Schema(description = "数据集ID")
    private String datasetId;
    @Schema(description = "样本ID")
    private Long itemId;
}
