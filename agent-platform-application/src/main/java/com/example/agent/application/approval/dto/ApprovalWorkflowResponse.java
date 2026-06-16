package com.example.agent.application.approval.dto;

import com.example.agent.domain.security.entity.ApprovalWorkflow;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批工单响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "审批工单")
public class ApprovalWorkflowResponse {

    @Schema(description = "审批业务 ID")
    private String approvalId;

    @Schema(description = "所属租户 ID")
    private String tenantId;

    @Schema(description = "关联工具 ID")
    private String toolId;

    @Schema(description = "关联会话 ID")
    private String conversationId;

    @Schema(description = "关联任务执行 ID")
    private String executionId;

    @Schema(description = "请求人 ID")
    private String requesterId;

    @Schema(description = "审批人 ID")
    private String approverId;

    @Schema(description = "审批标题")
    private String title;

    @Schema(description = "操作内容详情（JSON）")
    private String operationDetail;

    @Schema(description = "审批状态: PENDING/APPROVED/REJECTED/TIMEOUT/CANCELLED")
    private String status;

    @Schema(description = "状态标签")
    private String statusLabel;

    @Schema(description = "审批意见")
    private String approveComment;

    @Schema(description = "超时时间")
    private LocalDateTime timeoutAt;

    @Schema(description = "审批完成时间")
    private LocalDateTime approvedAt;

    @Schema(description = "是否已超时")
    private boolean expired;

    @Schema(description = "剩余时间（秒）")
    private long remainingSeconds;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt;

    public static ApprovalWorkflowResponse from(ApprovalWorkflow entity) {
        long remaining = entity.isPending()
                ? java.time.Duration.between(LocalDateTime.now(), entity.getTimeoutAt()).getSeconds()
                : 0;

        return ApprovalWorkflowResponse.builder()
                .approvalId(entity.getApprovalId())
                .tenantId(entity.getTenantId())
                .toolId(entity.getToolId())
                .conversationId(entity.getConversationId())
                .executionId(entity.getExecutionId())
                .requesterId(entity.getRequesterId())
                .approverId(entity.getApproverId())
                .title(entity.getTitle())
                .operationDetail(entity.getOperationDetail())
                .status(entity.getStatus().name())
                .statusLabel(statusLabel(entity.getStatus().name()))
                .approveComment(entity.getApproveComment())
                .timeoutAt(entity.getTimeoutAt())
                .approvedAt(entity.getApprovedAt())
                .expired(entity.isExpired())
                .remainingSeconds(Math.max(0, remaining))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static String statusLabel(String status) {
        return switch (status) {
            case "PENDING" -> "待审批";
            case "APPROVED" -> "已同意";
            case "REJECTED" -> "已拒绝";
            case "TIMEOUT" -> "已超时";
            case "CANCELLED" -> "已取消";
            default -> status;
        };
    }
}
