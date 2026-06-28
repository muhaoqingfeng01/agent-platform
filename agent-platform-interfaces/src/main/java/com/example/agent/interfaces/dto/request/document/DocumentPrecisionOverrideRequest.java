package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "文档精度覆盖请求")
public class DocumentPrecisionOverrideRequest {
    @NotBlank(message = "文档ID不能为空")
    @Schema(description = "文档ID")
    private String documentId;
    @Schema(description = "搜索参数覆盖JSON")
    private String searchParamsOverrideJson;
    @Schema(description = "多阶段参数覆盖JSON")
    private String multiStageOverrideJson;
}
