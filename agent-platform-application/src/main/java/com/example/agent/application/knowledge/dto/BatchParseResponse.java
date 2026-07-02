package com.example.agent.application.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量解析响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量解析结果")
public class BatchParseResponse {

    @Schema(description = "实际触发的文档数")
    private int triggered;

    @Schema(description = "请求的文档总数")
    private int total;
}
