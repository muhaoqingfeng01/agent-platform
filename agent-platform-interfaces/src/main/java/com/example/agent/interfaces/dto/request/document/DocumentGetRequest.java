package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文档获取请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Schema(description = "文档获取请求")
public class DocumentGetRequest {
    @NotBlank(message = "文档ID不能为空")
    @Schema(description = "文档ID")
    private String documentId;
}
