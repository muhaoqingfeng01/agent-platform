package com.example.agent.interfaces.dto.request.evaluation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "评测列表查询请求")
public class EvaluationListRequest {
    @Schema(description = "页码", example = "1")
    private int page = 1;
    @Schema(description = "每页大小", example = "20")
    private int size = 20;
}
