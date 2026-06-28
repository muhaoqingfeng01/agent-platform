package com.example.agent.interfaces.dto.request.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "知识检索请求")
public class KnowledgeSearchRequest {
    @Schema(description = "搜索关键词")
    private String query;
    @Schema(description = "知识库ID")
    private String knowledgeId;
    @Schema(description = "搜索配置")
    private Map<String, Object> searchConfig;
}
