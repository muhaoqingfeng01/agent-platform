package com.example.agent.interfaces.dto.request.evaluation;

import com.example.agent.application.evaluation.dto.DatasetAddItemCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 数据集添加样本请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "数据集添加样本请求")
public class DatasetAddItemRequest {

    @NotBlank(message = "数据集ID不能为空")
    @Schema(description = "数据集ID")
    private String datasetId;

    @Schema(description = "样本请求")
    private DatasetAddItemCommand itemRequest;
}
