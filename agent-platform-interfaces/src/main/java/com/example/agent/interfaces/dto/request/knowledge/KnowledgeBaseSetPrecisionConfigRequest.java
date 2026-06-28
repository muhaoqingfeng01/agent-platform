package com.example.agent.interfaces.dto.request.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "设置精度配置请求（含ID）")
public class KnowledgeBaseSetPrecisionConfigRequest {
    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String knowledgeId;
    @Schema(description = "索引类型")
    private String indexType;
    @Schema(description = "索引参数")
    private Map<String, Object> indexParams;
    @Schema(description = "搜索策略")
    private String searchStrategy;
    @Schema(description = "搜索参数")
    private Map<String, Object> searchParams;
    @Schema(description = "多阶段参数")
    private Map<String, Object> multiStageParams;
    @Schema(description = "监控参数")
    private Map<String, Object> monitoringParams;
}
