package com.example.agent.domain.security.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 安全事件实体 — 记录每次安全过滤/脱敏操作的审计信息.
 *
 * <p>只追加不修改，用于合规审计和攻击追溯。
 * <p>由 {@code SecurityEventRecorder} 在过滤/脱敏时自动写入。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class SecurityEvent {

    /** 自增主键 */
    private Long id;

    /** 所属租户 ID */
    private Long tenantId;

    /** 事件类型: INPUT_FILTER / OUTPUT_DESENSITIZE / INJECTION_DETECTED / JAILBREAK_DETECTED */
    private String eventType;

    /** 触发的规则 ID — 关联 t_sensitive_word.id */
    private Long ruleId;

    /** 关联会话 ID */
    private String conversationId;

    /** 关联消息 ID */
    private String messageId;

    /** 原始内容 */
    private String originalContent;

    /** 处理后内容（脱敏后或 null 表示被阻断） */
    private String processedContent;

    /** 匹配到的模式 */
    private String matchedPattern;

    /** 采取动作: LOG / WARN / BLOCK */
    private String actionTaken;

    /** 操作人 — 默认 SYSTEM */
    private String operator;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
