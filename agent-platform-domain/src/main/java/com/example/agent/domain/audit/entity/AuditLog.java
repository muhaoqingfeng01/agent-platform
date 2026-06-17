package com.example.agent.domain.audit.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 审计日志实体 — 记录全链路关键操作的不可变审计信息.
 *
 * <p>只追加不修改，用于合规审计和问题追溯。
 * <p>由 {@code AuditLogAspect} 在 {@code @Auditable} 注解方法执行时自动写入。
 * <p><b>大规模部署建议</b>：将数据写入 Elasticsearch，MySQL 仅保留近期热数据（30天）。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class AuditLog {

    /** 自增主键 */
    private Long id;

    /** 所属租户 ID */
    private Long tenantId;

    /** 全链路追踪 ID（串联 T9） */
    private String traceId;

    /** 关联会话 ID */
    private String conversationId;

    /** 操作者类型: USER / ASSISTANT / TOOL / SYSTEM */
    private String actorType;

    /** 操作者标识 */
    private String actorId;

    /** 动作类型（如 LLM_CALL、TOOL_INVOKE、RAG_RETRIEVE、INTENT_RECOGNITION） */
    private String action;

    /** 资源类型 */
    private String resourceType;

    /** 资源标识 */
    private String resourceId;

    /** 请求内容（JSON） */
    private String requestJson;

    /** 响应内容（JSON） */
    private String responseJson;

    /** 耗时（毫秒） */
    private Long durationMs;

    /** 操作状态: SUCCESS / FAILED / ERROR */
    private String status;

    /** 客户端 IP */
    private String ipAddress;

    /** User-Agent */
    private String userAgent;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
