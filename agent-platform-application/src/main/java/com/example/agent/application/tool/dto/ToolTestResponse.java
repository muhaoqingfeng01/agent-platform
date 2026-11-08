package com.example.agent.application.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 工具调用测试响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
public class ToolTestResponse {

    /** 调用是否成功 */
    @Schema(description = "调用是否成功")
    private boolean success;

    /** 调用返回结果 — 成功时包含工具返回值，失败时为 null */
    @Schema(description = "调用返回结果")
    private Object result;

    /** 调用耗时（毫秒） */
    @Schema(description = "调用耗时（毫秒）")
    private long durationMs;

    /** 错误信息 — 失败时包含异常详情 */
    @Schema(description = "错误信息")
    private String errorMessage;

    /** 调用记录 ID — 对应 t_tool_invocation_log.invocation_id */
    @Schema(description = "调用记录 ID")
    private String invocationId;

    /** 高风险工具已建审批、尚未真正调用 */
    @Schema(description = "是否等待审批")
    private boolean pendingApproval;

    /** 审批工单 ID */
    @Schema(description = "审批工单 ID")
    private String approvalId;
}
