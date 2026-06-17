package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审计日志 PO — 对应 t_audit_log 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogPO {

    private Long id;
    private String tenantId;
    private String traceId;
    private String conversationId;
    private String actorType;
    private String actorId;
    private String action;
    private String resourceType;
    private String resourceId;
    private String requestJson;
    private String responseJson;
    private Long durationMs;
    private String status;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}
