package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工具调用日志持久化对象 — 映射 t_tool_invocation_log 表.
 *
 * <p>input_json 和 output_json 存储为原始 JSON 字符串，
 * 无需反序列化为对象（前端直接展示 JSON 即可）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolInvocationLogPO {

    /** 自增主键 */
    private Long id;

    /** 调用记录业务唯一标识 — 格式: tlinv_{snowflake_id} */
    private String invocationId;

    /** 所属租户 ID */
    private String tenantId;

    /** 关联工具 ID — 对应 t_tool_registry.tool_id */
    private String toolId;

    /** 关联会话 ID — 对应 t_conversation.conversation_id */
    private String conversationId;

    /** 关联消息 ID — 对应 t_message.message_id */
    private String messageId;

    /** 关联任务执行 ID — 对应 t_task_execution.execution_id（T5 任务规划） */
    private String executionId;

    /** 输入参数 JSON */
    private String inputJson;

    /** 输出结果 JSON */
    private String outputJson;

    /** 调用状态 — SUCCESS / FAILED / TIMEOUT / REJECTED */
    private String status;

    /** 调用耗时（毫秒） */
    private Long durationMs;

    /** 错误信息 — 调用失败时记录 */
    private String errorMessage;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
