package com.example.agent.application.approval;

import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.approval.dto.CreateApprovalRequest;
import com.example.agent.application.task.DagExecutionService;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.security.entity.ApprovalWorkflow;
import com.example.agent.domain.security.repository.ApprovalWorkflowRepository;
import com.example.agent.domain.security.valueobject.ApprovalStatus;
import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.infrastructure.config.websocket.ConversationWebSocketHandler;
import com.example.agent.infrastructure.config.websocket.WebSocketMessage;
import com.example.agent.infrastructure.config.websocket.WebSocketMessageType;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批工单应用服务 — Facade 模式，管理高风险工具调用的审批流程.
 *
 * <p>核心流程：
 * <ol>
 *   <li>高风险工具调用 → 生成审批工单（PENDING）</li>
 *   <li>WebSocket 推送审批卡片给审批人</li>
 *   <li>审批人同意 → 继续执行 / 拒绝 → 终止任务</li>
 *   <li>超时 → 自动拒绝</li>
 * </ol>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalWorkflowApplicationService {

    private static final int TIMEOUT_MINUTES = 5;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ApprovalWorkflowRepository approvalRepository;
    private final ToolRegistryRepository toolRepository;
    private final DagExecutionService dagExecutor;
    private final ConversationWebSocketHandler wsHandler;

    /**
     * 创建审批工单 — 高风险工具调用前调用.
     *
     * @param request 包含 toolId, executionId, conversationId, params
     * @return 审批工单
     */
    @Transactional
    public ApprovalWorkflowResponse createApproval(CreateApprovalRequest request) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String requesterId = TenantContext.getCurrentUserId();

        // 获取工具信息
        ToolRegistry tool = toolRepository.findByToolId(request.getToolId())
                .orElseThrow(() -> new ResourceNotFoundException("工具不存在: " + request.getToolId()));

        // 构建操作详情 JSON
        String operationDetail = buildOperationDetail(tool.getName(), request.getParams(),
                request.getRiskLevel() != null ? request.getRiskLevel() : "HIGH");

        // 分配审批人（轮询或全局推送）
        String approverId = assignApprover(tenantId);

        ApprovalWorkflow approval = ApprovalWorkflow.builder()
                .approvalId(IdGenerator.generate("appr"))
                .tenantId(tenantId)
                .toolId(request.getToolId())
                .conversationId(request.getConversationId())
                .executionId(request.getExecutionId())
                .requesterId(requesterId)
                .approverId(approverId)
                .title("工具调用审批: " + tool.getName())
                .operationDetail(operationDetail)
                .status(ApprovalStatus.PENDING)
                .timeoutAt(LocalDateTime.now().plusMinutes(TIMEOUT_MINUTES))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        approvalRepository.save(approval);

        log.info("[Approval] 审批工单已创建: approvalId={}, toolId={}, requesterId={}, timeoutAt={}",
                approval.getApprovalId(), request.getToolId(), requesterId, approval.getTimeoutAt());

        // WebSocket 推送审批卡片
        pushApprovalCard(approval);

        return ApprovalWorkflowResponse.from(approval);
    }

    /**
     * 同意审批.
     *
     * @param approvalId 审批 ID
     * @param comment    审批意见
     */
    @Transactional
    public ApprovalWorkflowResponse approve(String approvalId, String comment) {
        ApprovalWorkflow approval = approvalRepository.findByApprovalId(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("审批工单不存在: " + approvalId));

        if (!approval.isPending()) {
            throw new BusinessException(409, "工单状态不是 PENDING，无法审批: " + approval.getStatus());
        }

        approval.approve(comment);
        approvalRepository.update(approval);

        log.info("[Approval] 审批已同意: approvalId={}, comment={}", approvalId, comment);

        // 回调执行引擎继续执行
        try {
            dagExecutor.resumeExecution(approval.getExecutionId());
        } catch (Exception e) {
            log.error("[Approval] 恢复执行失败: executionId={}", approval.getExecutionId(), e);
        }

        // 推送审批结果
        pushResult(approval, "APPROVED");

        return ApprovalWorkflowResponse.from(approval);
    }

    /**
     * 拒绝审批.
     *
     * @param approvalId 审批 ID
     * @param reason     拒绝原因
     */
    @Transactional
    public ApprovalWorkflowResponse reject(String approvalId, String reason) {
        ApprovalWorkflow approval = approvalRepository.findByApprovalId(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("审批工单不存在: " + approvalId));

        if (!approval.isPending()) {
            throw new BusinessException(409, "工单状态不是 PENDING，无法审批: " + approval.getStatus());
        }

        approval.reject(reason);
        approvalRepository.update(approval);

        log.info("[Approval] 审批已拒绝: approvalId={}, reason={}", approvalId, reason);

        // 终止任务
        try {
            dagExecutor.cancelExecution(approval.getExecutionId(), "审批拒绝: " + reason);
        } catch (Exception e) {
            log.error("[Approval] 取消执行失败: executionId={}", approval.getExecutionId(), e);
        }

        // 推送审批结果
        pushResult(approval, "REJECTED");

        return ApprovalWorkflowResponse.from(approval);
    }

    // ==================== 查询方法 ====================

    /** 我的待审批 */
    public List<ApprovalWorkflowResponse> listPendingByApprover(String approverId, int page, int size) {
        return approvalRepository.findByApprover(approverId, page, size).stream()
                .filter(ApprovalWorkflow::isPending)
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 我的已审批 */
    public List<ApprovalWorkflowResponse> listResolvedByApprover(String approverId, int page, int size) {
        return approvalRepository.findByApprover(approverId, page, size).stream()
                .filter(ApprovalWorkflow::isFinished)
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 我发起的 */
    public List<ApprovalWorkflowResponse> listByRequester(String requesterId, int page, int size) {
        return approvalRepository.findByRequester(requesterId, page, size).stream()
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 租户下所有工单 */
    public List<ApprovalWorkflowResponse> listByTenant(int page, int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return approvalRepository.findByTenant(tenantId, page, size).stream()
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 租户下按状态筛选 */
    public List<ApprovalWorkflowResponse> listByStatus(String status, int page, int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return approvalRepository.findByTenantAndStatus(tenantId, status, page, size).stream()
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 审批详情 */
    public ApprovalWorkflowResponse getByApprovalId(String approvalId) {
        ApprovalWorkflow approval = approvalRepository.findByApprovalId(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("审批工单不存在: " + approvalId));
        return ApprovalWorkflowResponse.from(approval);
    }

    /** 审批统计 */
    public Map<String, Object> stats() {
        Long tenantId = TenantContext.getCurrentTenantId();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("pending", approvalRepository.countByStatus(tenantId, ApprovalStatus.PENDING.name()));
        stats.put("approved", approvalRepository.countByStatus(tenantId, ApprovalStatus.APPROVED.name()));
        stats.put("rejected", approvalRepository.countByStatus(tenantId, ApprovalStatus.REJECTED.name()));
        stats.put("timeout", approvalRepository.countByStatus(tenantId, ApprovalStatus.TIMEOUT.name()));
        stats.put("total", approvalRepository.countByTenant(tenantId));
        return stats;
    }

    // ==================== 私有方法 ====================

    /** 构建操作详情 JSON */
    private String buildOperationDetail(String toolName, Map<String, Object> params, String riskLevel) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("toolName", toolName);
        detail.put("params", params);
        detail.put("riskLevel", riskLevel);
        detail.put("timeoutMinutes", TIMEOUT_MINUTES);
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            log.warn("[Approval] 序列化操作详情失败", e);
            return "{}";
        }
    }

    /** 分配审批人（当前简化实现：推送给所有在线用户） */
    private String assignApprover(Long tenantId) {
        // TODO: 实现轮询分配或角色查询
        // 当前返回 null 表示推送给所有审批人
        return null;
    }

    /** WebSocket 推送审批卡片 */
    private void pushApprovalCard(ApprovalWorkflow approval) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("approvalId", approval.getApprovalId());
            payload.put("title", approval.getTitle());
            payload.put("toolId", approval.getToolId());
            payload.put("requesterId", approval.getRequesterId());
            payload.put("status", approval.getStatus().name());
            payload.put("timeoutAt", approval.getTimeoutAt().toString());
            payload.put("operationDetail", approval.getOperationDetail());

            WebSocketMessage msg = WebSocketMessage.builder()
                    .type(WebSocketMessageType.APPROVAL_CARD)
                    .payload(payload)
                    .timestamp(System.currentTimeMillis())
                    .build();

            // 推送给指定审批人或广播给所有在线用户
            if (approval.getApproverId() != null) {
                wsHandler.pushMessage(approval.getApproverId(), msg);
            } else {
                wsHandler.broadcast(msg);
            }

            log.debug("[Approval] 审批卡片已推送: approvalId={}", approval.getApprovalId());
        } catch (Exception e) {
            log.error("[Approval] WebSocket 推送失败: approvalId={}", approval.getApprovalId(), e);
        }
    }

    /** WebSocket 推送审批结果 */
    private void pushResult(ApprovalWorkflow approval, String result) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("approvalId", approval.getApprovalId());
            payload.put("result", result);
            payload.put("comment", approval.getApproveComment());
            payload.put("approvedAt", approval.getApprovedAt() != null
                    ? approval.getApprovedAt().toString() : null);

            WebSocketMessage msg = WebSocketMessage.builder()
                    .type(WebSocketMessageType.APPROVAL_RESULT)
                    .payload(payload)
                    .timestamp(System.currentTimeMillis())
                    .build();

            // 通知请求人
            wsHandler.pushMessage(approval.getRequesterId(), msg);

            // 如果是指定审批人，也通知审批人
            if (approval.getApproverId() != null) {
                wsHandler.pushMessage(approval.getApproverId(), msg);
            }

            log.debug("[Approval] 审批结果已推送: approvalId={}, result={}", approval.getApprovalId(), result);
        } catch (Exception e) {
            log.error("[Approval] 结果推送失败: approvalId={}", approval.getApprovalId(), e);
        }
    }
}
