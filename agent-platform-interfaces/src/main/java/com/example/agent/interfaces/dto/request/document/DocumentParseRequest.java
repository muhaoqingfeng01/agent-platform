package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "触发文档解析请求")
public class DocumentParseRequest {
    @NotBlank(message = "文档ID不能为空")
    @Schema(description = "文档ID")
    private String documentId;
}
