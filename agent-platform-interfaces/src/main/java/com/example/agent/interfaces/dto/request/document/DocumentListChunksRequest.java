package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "文档切片列表请求")
public class DocumentListChunksRequest {
    @NotBlank(message = "文档ID不能为空")
    @Schema(description = "文档ID")
    private String documentId;
    @Schema(description = "偏移量", example = "0")
    private int offset = 0;
    @Schema(description = "限制数量", example = "50")
    private int limit = 50;
}
