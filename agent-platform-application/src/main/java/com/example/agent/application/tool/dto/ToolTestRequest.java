package com.example.agent.application.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 测试工具调用请求 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class ToolTestRequest {

    /** 要测试的工具 ID */
    @NotBlank(message = "工具 ID 不能为空")
    @Schema(description = "工具 ID", example = "tool_1896573294812348416")
    private String toolId;

    /** 测试参数 — 根据工具 Schema 传入的键值对 */
    @Schema(description = "测试参数", example = "{\"status\":\"pending\",\"start_date\":\"2026-01-01\"}")
    private Map<String, Object> params;
}
