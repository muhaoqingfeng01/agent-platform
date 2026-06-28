package com.example.agent.interfaces.dto.request.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "人工标注请求")
public class KnowledgeHitFeedbackRequest {
    @NotNull(message = "命中记录ID不能为空")
    @Schema(description = "命中记录ID")
    private Long id;
    @Schema(description = "反馈类型: EXCELLENT/NEEDS_FIX/SUPPLEMENT")
    private String feedback;
    @Schema(description = "备注")
    private String note;
}
