package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量解析文档请求")
public class DocumentBatchParseRequest {
    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String knowledgeId;
    @Schema(description = "文档ID列表")
    private List<String> documentIds;
}
