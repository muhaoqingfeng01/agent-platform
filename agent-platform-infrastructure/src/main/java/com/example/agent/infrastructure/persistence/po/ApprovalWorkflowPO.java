package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审批工单持久化对象 — 映射 t_approval_workflow 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalWorkflowPO {

    private Long id;
    private String tenantId;
    private String approvalId;
    private String toolId;
    private String conversationId;
    private String executionId;
    private String requesterId;
    private String approverId;
    private String title;
    private String operationDetail;
    private String status;
    private String approveComment;
    private LocalDateTime timeoutAt;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
