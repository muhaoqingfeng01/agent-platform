package com.example.agent.domain.security.entity;

import com.example.agent.domain.security.valueobject.ApprovalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 审批工单聚合根 — 管理高风险工具调用的人机协同审批流程.
 *
 * <p>状态机：PENDING → APPROVED / REJECTED / TIMEOUT / CANCELLED
 *
 * <p>领域不变量：
 * <ul>
 *   <li>只有 PENDING 状态的工单可以被审批</li>
 *   <li>超时时间必须在创建时设定，不可修改</li>
 *   <li>审批完成后（APPROVED/REJECTED）不可再变更状态</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class ApprovalWorkflow {

    /** 自增主键 */
    private Long id;

    /** 所属租户 ID */
    private String tenantId;

    /** 审批业务唯一标识 — 格式: appr_{snowflake_id} */
    private String approvalId;

    /** 关联工具 ID */
    private String toolId;

    /** 关联会话 ID */
    private String conversationId;

    /** 关联任务执行 ID */
    private String executionId;

    /** 请求人 user_id */
    private String requesterId;

    /** 审批人 user_id — NULL 表示待分配 */
    private String approverId;

    /** 审批标题 */
    private String title;

    /** 操作内容详情 — JSON 字符串 */
    private String operationDetail;

    /** 审批状态: PENDING / APPROVED / REJECTED / TIMEOUT / CANCELLED */
    private ApprovalStatus status;

    /** 审批意见 */
    private String approveComment;

    /** 超时时间 */
    private LocalDateTime timeoutAt;

    /** 审批完成时间 */
    private LocalDateTime approvedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;

    // ==================== 领域行为方法（状态机） ====================

    /**
     * 同意审批.
     *
     * @param comment 审批意见
     * @throws IllegalStateException 如果工单不是 PENDING 状态
     */
    public void approve(String comment) {
        assertPending();
        this.status = ApprovalStatus.APPROVED;
        this.approveComment = comment;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 拒绝审批.
     *
     * @param reason 拒绝原因
     * @throws IllegalStateException 如果工单不是 PENDING 状态
     */
    public void reject(String reason) {
        assertPending();
        this.status = ApprovalStatus.REJECTED;
        this.approveComment = reason;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 超时自动拒绝.
     *
     * @throws IllegalStateException 如果工单不是 PENDING 状态
     */
    public void timeout() {
        assertPending();
        this.status = ApprovalStatus.TIMEOUT;
        this.approveComment = "审批超时，系统自动拒绝";
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 取消工单（任务被取消时）.
     *
     * @throws IllegalStateException 如果工单不是 PENDING 状态
     */
    public void cancel() {
        assertPending();
        this.status = ApprovalStatus.CANCELLED;
        this.approveComment = "关联任务已取消";
        this.approvedAt = LocalDateTime.now();
    }

    /** 是否处于待审批状态 */
    public boolean isPending() {
        return this.status == ApprovalStatus.PENDING;
    }

    /** 是否已完成（同意/拒绝/超时/取消） */
    public boolean isFinished() {
        return this.status != ApprovalStatus.PENDING;
    }

    /** 是否已超时 */
    public boolean isExpired() {
        return this.isPending() && LocalDateTime.now().isAfter(this.timeoutAt);
    }

    // ==================== 私有方法 ====================

    private void assertPending() {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException(
                    "工单状态不是 PENDING，无法操作: approvalId=" + this.approvalId
                    + ", currentStatus=" + this.status);
        }
    }
}
