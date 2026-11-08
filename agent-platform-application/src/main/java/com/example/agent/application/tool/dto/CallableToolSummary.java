package com.example.agent.application.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 对话可调用工具摘要 — 供 LLM 决策选用.
 */
@Data
@Builder
@Schema(description = "对话可调用工具摘要")
public class CallableToolSummary {

    @Schema(description = "工具业务 ID")
    private String toolId;

    @Schema(description = "工具名称")
    private String name;

    @Schema(description = "功能描述")
    private String description;

    @Schema(description = "工具类型 MCP/HTTP")
    private String toolType;

    @Schema(description = "输入 JSON Schema")
    private String inputSchema;

    @Schema(description = "是否需要审批")
    private boolean requireApproval;
}
