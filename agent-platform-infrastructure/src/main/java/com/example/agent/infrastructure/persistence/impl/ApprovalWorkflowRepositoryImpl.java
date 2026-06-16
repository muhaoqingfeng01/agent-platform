package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.security.entity.ApprovalWorkflow;
import com.example.agent.domain.security.repository.ApprovalWorkflowRepository;
import com.example.agent.domain.security.valueobject.ApprovalStatus;
import com.example.agent.infrastructure.persistence.mapper.ApprovalWorkflowMapper;
import com.example.agent.infrastructure.persistence.po.ApprovalWorkflowPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 审批工单仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ApprovalWorkflowRepositoryImpl implements ApprovalWorkflowRepository {

    private final ApprovalWorkflowMapper mapper;

    @Override
    public void save(ApprovalWorkflow workflow) {
        mapper.insert(toPO(workflow));
    }

    @Override
    public void update(ApprovalWorkflow workflow) {
        mapper.update(toPO(workflow));
    }

    @Override
    public Optional<ApprovalWorkflow> findByApprovalId(String approvalId) {
        ApprovalWorkflowPO po = mapper.selectByApprovalId(approvalId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ApprovalWorkflow> findByTenant(String tenantId, int page, int size) {
        return mapper.selectByTenant(tenantId, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public long countByTenant(String tenantId) {
        return mapper.countByTenant(tenantId);
    }

    @Override
    public List<ApprovalWorkflow> findByRequester(String requesterId, int page, int size) {
        return mapper.selectByRequester(requesterId, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<ApprovalWorkflow> findByApprover(String approverId, int page, int size) {
        return mapper.selectByApprover(approverId, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<ApprovalWorkflow> findTimeoutPending(LocalDateTime now) {
        return mapper.selectTimeoutPending(now).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public long countByStatus(String tenantId, String status) {
        return mapper.countByStatus(tenantId, status);
    }

    @Override
    public List<ApprovalWorkflow> findByTenantAndStatus(String tenantId, String status, int page, int size) {
        return mapper.selectByTenantAndStatus(tenantId, status, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    // ==================== 映射方法 ====================

    private ApprovalWorkflow toDomain(ApprovalWorkflowPO po) {
        return ApprovalWorkflow.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .approvalId(po.getApprovalId())
                .toolId(po.getToolId())
                .conversationId(po.getConversationId())
                .executionId(po.getExecutionId())
                .requesterId(po.getRequesterId())
                .approverId(po.getApproverId())
                .title(po.getTitle())
                .operationDetail(po.getOperationDetail())
                .status(ApprovalStatus.fromCode(po.getStatus()))
                .approveComment(po.getApproveComment())
                .timeoutAt(po.getTimeoutAt())
                .approvedAt(po.getApprovedAt())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private ApprovalWorkflowPO toPO(ApprovalWorkflow workflow) {
        return ApprovalWorkflowPO.builder()
                .id(workflow.getId())
                .tenantId(workflow.getTenantId())
                .approvalId(workflow.getApprovalId())
                .toolId(workflow.getToolId())
                .conversationId(workflow.getConversationId())
                .executionId(workflow.getExecutionId())
                .requesterId(workflow.getRequesterId())
                .approverId(workflow.getApproverId())
                .title(workflow.getTitle())
                .operationDetail(workflow.getOperationDetail())
                .status(workflow.getStatus().name())
                .approveComment(workflow.getApproveComment())
                .timeoutAt(workflow.getTimeoutAt())
                .approvedAt(workflow.getApprovedAt())
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();
    }
}
