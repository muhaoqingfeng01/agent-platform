package com.example.agent.domain.tool.entity;

import com.example.agent.domain.tool.valueobject.InvocationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 工具调用日志实体 — 记录每次工具调用的完整信息.
 *
 * <p>每条日志关联到具体的工具、会话、消息和任务执行上下文，
 * 为 T9 全链路追踪和 T12 效果评估提供数据基础.
 * 调用成功时 outputJson 包含返回值，失败时 errorMessage 记录错误信息.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class ToolInvocationLog {

    /** 调用记录业务唯一标识 — 格式: tlinv_{snowflake_id} */
    private String invocationId;

    /** 所属租户 ID — 多租户隔离 */
    private String tenantId;

    /** 关联工具 ID — 对应 t_tool_registry.tool_id */
    private String toolId;

    /** 关联会话 ID — 对应 t_conversation.conversation_id，记录本次调用发生在哪个对话中 */
    private String conversationId;

    /** 关联消息 ID — 对应 t_message.message_id，记录触发本次调用的具体消息 */
    private String messageId;

    /** 关联任务执行 ID — 对应 t_task_execution.execution_id（T5 任务规划），记录所属的 DAG 执行实例 */
    private String executionId;

    /** 输入参数 JSON — 调用工具时传入的参数 */
    private String inputJson;

    /** 输出结果 JSON — 工具返回的原始结果 */
    private String outputJson;

    /** 调用状态 — SUCCESS / FAILED / TIMEOUT / REJECTED */
    private InvocationStatus status;

    /** 调用耗时（毫秒） — 从发起调用到收到响应的总耗时 */
    private Long durationMs;

    /** 错误信息 — 调用失败时的异常信息 */
    private String errorMessage;

    /** 创建时间 — 调用发生的时间点 */
    private LocalDateTime createdAt;
}
