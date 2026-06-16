package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 安全事件持久化对象 — 映射 t_security_event 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEventPO {

    private Long id;
    private String tenantId;
    private String eventType;
    private Long ruleId;
    private String conversationId;
    private String messageId;
    private String originalContent;
    private String processedContent;
    private String matchedPattern;
    private String actionTaken;
    private String operator;
    private LocalDateTime createdAt;
}
