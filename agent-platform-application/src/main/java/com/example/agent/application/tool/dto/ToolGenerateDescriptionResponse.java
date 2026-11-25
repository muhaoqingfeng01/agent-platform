package com.example.agent.application.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 工具描述润色响应 — 仅预览，不落库.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "工具描述润色结果")
public class ToolGenerateDescriptionResponse {

    @Schema(description = "润色后的工具描述")
    private String description;
}
