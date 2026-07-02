package com.example.agent.application.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库文档统计响应 DTO — 按文档状态分组计数.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库文档统计")
public class KnowledgeBaseStatsResponse {

    @Schema(description = "待解析数量")
    private long pendingParse;

    @Schema(description = "解析中数量")
    private long parsing;

    @Schema(description = "切分中数量")
    private long chunking;

    @Schema(description = "向量化中数量")
    private long embedding;

    @Schema(description = "已解析数量")
    private long parsed;

    @Schema(description = "已弃用数量")
    private long deprecated;

    @Schema(description = "失败数量")
    private long failed;

    @Schema(description = "文档总数")
    private long total;
}
