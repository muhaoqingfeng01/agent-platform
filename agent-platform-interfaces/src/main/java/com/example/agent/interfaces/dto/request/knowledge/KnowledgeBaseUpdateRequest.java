package com.example.agent.interfaces.dto.request.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "更新知识库请求（含ID）")
public class KnowledgeBaseUpdateRequest {
    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String knowledgeId;
    @Schema(description = "知识库名称")
    private String name;
    @Schema(description = "知识库描述")
    private String description;
}
