package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文档列表查询请求")
public class DocumentListRequest {
    @Schema(description = "知识库ID")
    private String knowledgeId;
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页大小", example = "20")
    private int size = 20;
}
