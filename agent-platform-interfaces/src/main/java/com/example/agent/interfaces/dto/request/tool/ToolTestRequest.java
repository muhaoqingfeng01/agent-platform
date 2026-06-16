package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
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

    /** 测试参数 — 根据工具 Schema 传入的键值对 */
    @Schema(description = "测试参数", example = "{\"status\":\"pending\",\"start_date\":\"2026-01-01\"}")
    private Map<String, Object> params;
}
