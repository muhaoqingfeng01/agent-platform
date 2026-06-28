package com.example.agent.interfaces.dto.request.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文档上传请求 DTO — 配合 multipart/form-data 中的 metadata part 使用
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Schema(description = "文档上传请求")
public class DocumentUploadRequest {

    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String knowledgeId;

    @Schema(description = "切片策略")
    private String chunkStrategy;

    @Schema(description = "切片大小")
    private Integer chunkSize;

    @Schema(description = "切片重叠")
    private Integer chunkOverlap;

    @Schema(description = "切片配置JSON")
    private String chunkConfig;
}
