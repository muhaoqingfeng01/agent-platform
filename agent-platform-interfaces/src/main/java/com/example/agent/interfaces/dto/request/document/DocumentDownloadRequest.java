package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "下载文档请求")
public class DocumentDownloadRequest {
    @NotBlank(message = "文档ID不能为空")
    @Schema(description = "文档ID")
    private String documentId;
}
