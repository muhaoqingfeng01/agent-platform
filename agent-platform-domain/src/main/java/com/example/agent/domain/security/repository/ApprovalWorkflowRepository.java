package com.example.agent.domain.security.repository;

import com.example.agent.domain.security.entity.ApprovalWorkflow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 审批工单仓储接口 — Domain 层定义，Infrastructure 层实现.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface ApprovalWorkflowRepository {

    /** 保存 */
    void save(ApprovalWorkflow workflow);

    /** 更新 */
    void update(ApprovalWorkflow workflow);

    /** 按审批 ID 查询 */
    Optional<ApprovalWorkflow> findByApprovalId(String approvalId);

    /** 按租户分页查询 */
    List<ApprovalWorkflow> findByTenant(String tenantId, int page, int size);

    /** 统计租户下的工单数 */
    long countByTenant(String tenantId);

    /** 按请求人查询 */
    List<ApprovalWorkflow> findByRequester(String requesterId, int page, int size);

    /** 按审批人查询 */
    List<ApprovalWorkflow> findByApprover(String approverId, int page, int size);

    /** 查询超时的待审批工单 */
    List<ApprovalWorkflow> findTimeoutPending(LocalDateTime now);

    /** 按状态统计（租户维度） */
    long countByStatus(String tenantId, String status);

    /** 按状态查询 */
    List<ApprovalWorkflow> findByTenantAndStatus(String tenantId, String status, int page, int size);
}
