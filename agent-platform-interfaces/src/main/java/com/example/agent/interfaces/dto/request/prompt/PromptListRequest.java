package com.example.agent.interfaces.dto.request.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "提示词列表查询请求")
public class PromptListRequest {
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页大小", example = "20")
    private int size = 20;
    @Schema(description = "状态过滤: DRAFT/PUBLISHED/ARCHIVED")
    private String status;
}
