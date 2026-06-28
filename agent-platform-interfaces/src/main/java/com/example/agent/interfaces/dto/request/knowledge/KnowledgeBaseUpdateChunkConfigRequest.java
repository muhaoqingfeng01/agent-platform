package com.example.agent.interfaces.dto.request.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "更新切片配置请求（含ID）")
public class KnowledgeBaseUpdateChunkConfigRequest {
    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String knowledgeId;
    @Schema(description = "默认切片策略编码")
    private String defaultChunkStrategy;
    @Schema(description = "切片配置JSON")
    private String chunkConfigJson;
}
