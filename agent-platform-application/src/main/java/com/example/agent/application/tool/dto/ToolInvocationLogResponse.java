package com.example.agent.application.tool.dto;

import com.example.agent.domain.tool.entity.ToolInvocationLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工具调用日志响应 DTO — 用于 API 返回调用历史.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class ToolInvocationLogResponse {

    /** 调用记录业务唯一标识 */
    @Schema(description = "调用记录 ID")
    private String invocationId;

    /** 工具 ID */
    @Schema(description = "工具 ID")
    private String toolId;

    /** 工具名称 — 冗余字段便于前端展示，无需二次查询 */
    @Schema(description = "工具名称")
    private String toolName;

    /** 关联会话 ID */
    @Schema(description = "关联会话 ID")
    private String conversationId;

    /** 关联消息 ID */
    @Schema(description = "关联消息 ID")
    private String messageId;

    /** 关联任务执行 ID */
    @Schema(description = "关联任务执行 ID")
    private String executionId;

    /** 输入参数 JSON */
    @Schema(description = "输入参数 JSON")
    private String inputJson;

    /** 输出结果 JSON */
    @Schema(description = "输出结果 JSON")
    private String outputJson;

    /** 调用状态代码 — SUCCESS / FAILED / TIMEOUT / REJECTED */
    @Schema(description = "调用状态代码")
    private String status;

    /** 调用状态中文标签 */
    @Schema(description = "调用状态中文")
    private String statusLabel;

    /** 调用耗时（毫秒） */
    @Schema(description = "调用耗时（毫秒）")
    private Long durationMs;

    /** 错误信息 */
    @Schema(description = "错误信息")
    private String errorMessage;

    /** 调用时间 */
    @Schema(description = "调用时间")
    private LocalDateTime createdAt;

    /**
     * 从领域实体和工具名称构建响应 DTO — 工厂方法.
     *
     * @param entity   调用日志领域实体
     * @param toolName 工具名称（冗余字段，避免前端二次查询）
     * @return 调用日志响应 DTO
     */
    public static ToolInvocationLogResponse from(ToolInvocationLog entity, String toolName) {
        ToolInvocationLogResponse r = new ToolInvocationLogResponse();
        r.setInvocationId(entity.getInvocationId());
        r.setToolId(entity.getToolId());
        r.setToolName(toolName);
        r.setConversationId(entity.getConversationId());
        r.setMessageId(entity.getMessageId());
        r.setExecutionId(entity.getExecutionId());
        r.setInputJson(entity.getInputJson());
        r.setOutputJson(entity.getOutputJson());
        r.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        r.setStatusLabel(entity.getStatus() != null ? entity.getStatus().toChinese() : null);
        r.setDurationMs(entity.getDurationMs());
        r.setErrorMessage(entity.getErrorMessage());
        r.setCreatedAt(entity.getCreatedAt());
        return r;
    }
}
