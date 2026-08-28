package com.example.agent.application.approval;

import com.example.agent.application.approval.dto.ApprovalStatsResponse;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.approval.dto.ApprovalCreateCommand;
import com.example.agent.application.task.DagExecutionService;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.security.entity.ApprovalWorkflow;
import com.example.agent.domain.security.repository.ApprovalWorkflowRepository;
import com.example.agent.domain.security.valueobject.ApprovalStatus;
import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.infrastructure.config.nacos.SecurityConfig;
import com.example.agent.infrastructure.config.websocket.ConversationWebSocketHandler;
import com.example.agent.infrastructure.config.websocket.WebSocketMessage;
import com.example.agent.infrastructure.config.websocket.WebSocketMessageType;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ApprovalWorkflowRepository approvalRepository;
    private final ToolRegistryRepository toolRepository;
    private final DagExecutionService dagExecutor;
    private final ConversationWebSocketHandler wsHandler;
    /** 🆕 P6 配置治理子方案03: 审批超时从 Nacos 动态读取 */
    private final SecurityConfig securityConfig;

    /**
     * 创建审批工单 — 高风险工具调用前调用.
     *
     * @param request 包含 toolId, executionId, conversationId, params
     * @return 审批工单
     */
    @Transactional
    public ApprovalWorkflowResponse createApproval(ApprovalCreateCommand request) {
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
                .timeoutAt(LocalDateTime.now().plusMinutes(securityConfig.getApprovalTimeoutMinutes()))
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
     * 为 DAG 高风险步骤创建审批工单（不依赖工具注册表）.
     * <p>toolId 字段写入动作类型，供卡片展示与审计。
     */
    @Transactional
    public ApprovalWorkflowResponse createActionApproval(String executionId, String conversationId,
                                                         String actionType, String stepId,
                                                         Map<String, Object> params) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String requesterId = TenantContext.getCurrentUserId();
        String action = actionType != null ? actionType : "unknown_action";

        String operationDetail = buildOperationDetail(action, params != null ? params : Map.of(), "HIGH");
        String approverId = assignApprover(tenantId);

        ApprovalWorkflow approval = ApprovalWorkflow.builder()
                .approvalId(IdGenerator.generate("appr"))
                .tenantId(tenantId)
                .toolId(action)
                .conversationId(conversationId)
                .executionId(executionId)
                .requesterId(requesterId)
                .approverId(approverId)
                .title("任务步骤审批: " + action + "（步骤 " + stepId + "）")
                .operationDetail(operationDetail)
                .status(ApprovalStatus.PENDING)
                .timeoutAt(LocalDateTime.now().plusMinutes(securityConfig.getApprovalTimeoutMinutes()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        approvalRepository.save(approval);

        log.info("[Approval] DAG 步骤审批工单已创建: approvalId={}, executionId={}, stepId={}, action={}",
                approval.getApprovalId(), executionId, stepId, action);

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

        // 回调执行引擎继续执行（B6 未接线时 executionId 可为空）
        if (approval.getExecutionId() != null && !approval.getExecutionId().isBlank()) {
            try {
                dagExecutor.resumeExecution(approval.getExecutionId());
            } catch (Exception e) {
                log.error("[Approval] 恢复执行失败: executionId={}", approval.getExecutionId(), e);
                throw new BusinessException(500, "恢复执行失败: " + approval.getExecutionId(), e);
            }
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

        String rejectReason = reason == null || reason.isBlank() ? "拒绝" : reason;
        approval.reject(rejectReason);
        approvalRepository.update(approval);

        log.info("[Approval] 审批已拒绝: approvalId={}, reason={}", approvalId, rejectReason);

        // 终止任务（B6 未接线时 executionId 可为空）
        if (approval.getExecutionId() != null && !approval.getExecutionId().isBlank()) {
            try {
                dagExecutor.cancelExecution(approval.getExecutionId(), "审批拒绝: " + rejectReason);
            } catch (Exception e) {
                log.error("[Approval] 取消执行失败: executionId={}", approval.getExecutionId(), e);
                throw new BusinessException(500, "取消执行失败: " + approval.getExecutionId(), e);
            }
        }

        // 推送审批结果
        pushResult(approval, "REJECTED");

        return ApprovalWorkflowResponse.from(approval);
    }

    // ==================== 查询方法 ====================

    /**
     * 当前用户可见的待审批工单（断线重连补卡片）.
     *
     * @param conversationId 可选；传入则只返回该会话下 PENDING
     */
    public List<ApprovalWorkflowResponse> listPending(String conversationId) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String userId = TenantContext.getCurrentUserId();
        return approvalRepository.findPending(tenantId, userId, conversationId, 0, 100).stream()
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 我的待审批 */
    public List<ApprovalWorkflowResponse> listPendingByApprover(String approverId, int page, int size) {
        String userId = resolveUserId(approverId);
        Long tenantId = TenantContext.getCurrentTenantId();
        return approvalRepository.findPending(tenantId, userId, null, page, size).stream()
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 我的已审批 */
    public List<ApprovalWorkflowResponse> listResolvedByApprover(String approverId, int page, int size) {
        return approvalRepository.findByApprover(resolveUserId(approverId), page, size).stream()
                .filter(ApprovalWorkflow::isFinished)
                .map(ApprovalWorkflowResponse::from)
                .toList();
    }

    /** 我发起的 */
    public List<ApprovalWorkflowResponse> listByRequester(String requesterId, int page, int size) {
        return approvalRepository.findByRequester(resolveUserId(requesterId), page, size).stream()
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
    public ApprovalStatsResponse stats() {
        Long tenantId = TenantContext.getCurrentTenantId();
        return ApprovalStatsResponse.builder()
                .pending(approvalRepository.countByStatus(tenantId, ApprovalStatus.PENDING.name()))
                .approved(approvalRepository.countByStatus(tenantId, ApprovalStatus.APPROVED.name()))
                .rejected(approvalRepository.countByStatus(tenantId, ApprovalStatus.REJECTED.name()))
                .timeout(approvalRepository.countByStatus(tenantId, ApprovalStatus.TIMEOUT.name()))
                .total(approvalRepository.countByTenant(tenantId))
                .build();
    }

    // ==================== 私有方法 ====================

    /** 构建操作详情 JSON */
    private String buildOperationDetail(String toolName, Map<String, Object> params, String riskLevel) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("toolName", toolName);
        detail.put("params", params);
        detail.put("riskLevel", riskLevel);
        detail.put("timeoutMinutes", securityConfig.getApprovalTimeoutMinutes());
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

    /** 未显式传入时使用当前登录用户 */
    private String resolveUserId(String explicitId) {
        if (explicitId != null && !explicitId.isBlank()) {
            return explicitId;
        }
        return TenantContext.getCurrentUserId();
    }

    /** WebSocket 推送审批卡片 */
    private void pushApprovalCard(ApprovalWorkflow approval) {
        try {
            WebSocketMessage msg = WebSocketMessage.builder()
                    .type(WebSocketMessageType.APPROVAL_CARD)
                    .payload(buildCardPayload(approval))
                    .timestamp(System.currentTimeMillis())
                    .build();

            if (approval.getApproverId() != null) {
                wsHandler.pushMessage(approval.getApproverId(), msg);
                if (approval.getRequesterId() != null
                        && !approval.getRequesterId().equals(approval.getApproverId())) {
                    wsHandler.pushMessage(approval.getRequesterId(), msg);
                }
            } else {
                wsHandler.broadcast(msg);
            }

            log.debug("[Approval] 审批卡片已推送: approvalId={}", approval.getApprovalId());
        } catch (Exception e) {
            log.error("[Approval] WebSocket 推送失败: approvalId={}", approval.getApprovalId(), e);
        }
    }

    /** WebSocket 推送审批结果（同意 / 拒绝 / 超时） */
    public void pushResult(ApprovalWorkflow approval, String result) {
        try {
            WebSocketMessage msg = WebSocketMessage.builder()
                    .type(WebSocketMessageType.APPROVAL_RESULT)
                    .payload(buildResultPayload(approval, result))
                    .timestamp(System.currentTimeMillis())
                    .build();

            if (approval.getApproverId() != null) {
                if (approval.getRequesterId() != null) {
                    wsHandler.pushMessage(approval.getRequesterId(), msg);
                }
                wsHandler.pushMessage(approval.getApproverId(), msg);
            } else {
                // 建单时广播了卡片，结果也必须广播，否则其它在线端按钮不会禁用
                wsHandler.broadcast(msg);
            }

            log.debug("[Approval] 审批结果已推送: approvalId={}, result={}", approval.getApprovalId(), result);
        } catch (Exception e) {
            log.error("[Approval] 结果推送失败: approvalId={}", approval.getApprovalId(), e);
        }
    }

    private Map<String, Object> buildCardPayload(ApprovalWorkflow approval) {
        Map<String, Object> detailMap = parseOperationDetail(approval.getOperationDetail());
        String riskLevel = String.valueOf(detailMap.getOrDefault("riskLevel", "HIGH")).toLowerCase();
        long remaining = approval.isPending()
                ? java.time.Duration.between(java.time.LocalDateTime.now(), approval.getTimeoutAt()).getSeconds()
                : 0;
        int timeoutSeconds = securityConfig.getApprovalTimeoutMinutes() * 60;
        Object timeoutMinutes = detailMap.get("timeoutMinutes");
        if (timeoutMinutes instanceof Number n) {
            timeoutSeconds = n.intValue() * 60;
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("toolName", detailMap.getOrDefault("toolName", approval.getToolId()));
        metadata.put("toolParams", detailMap.getOrDefault("params", Map.of()));
        metadata.put("affectedResources", detailMap.getOrDefault("affectedResources", List.of()));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("approvalId", approval.getApprovalId());
        payload.put("conversationId", approval.getConversationId());
        payload.put("executionId", approval.getExecutionId());
        payload.put("title", approval.getTitle());
        payload.put("detail", buildReadableDetail(approval, detailMap));
        payload.put("riskLevel", riskLevel);
        payload.put("requestedBy", approval.getRequesterId());
        payload.put("options", List.of("同意", "拒绝"));
        payload.put("timeout", timeoutSeconds);
        payload.put("remainingSeconds", Math.max(0, remaining));
        payload.put("timeoutAt", TimeConverters.toEpochMilli(approval.getTimeoutAt()));
        payload.put("createdAt", TimeConverters.toEpochMilli(approval.getCreatedAt()));
        payload.put("status", approval.getStatus().name());
        payload.put("toolId", approval.getToolId());
        payload.put("operationDetail", approval.getOperationDetail());
        payload.put("metadata", metadata);
        return payload;
    }

    private Map<String, Object> buildResultPayload(ApprovalWorkflow approval, String result) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("approvalId", approval.getApprovalId());
        payload.put("conversationId", approval.getConversationId());
        payload.put("result", result);
        payload.put("status", result);
        payload.put("comment", approval.getApproveComment());
        payload.put("approvedAt", TimeConverters.toEpochMilli(approval.getApprovedAt()));
        payload.put("toolId", approval.getToolId());
        return payload;
    }

    private Map<String, Object> parseOperationDetail(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("[Approval] 解析 operationDetail 失败", e);
            return Map.of();
        }
    }

    private String buildReadableDetail(ApprovalWorkflow approval, Map<String, Object> detailMap) {
        Object toolName = detailMap.get("toolName");
        Object params = detailMap.get("params");
        if (toolName != null && params != null) {
            return "即将调用工具 [" + toolName + "]，参数: " + params;
        }
        if (approval.getOperationDetail() != null && !approval.getOperationDetail().isBlank()
                && !approval.getOperationDetail().startsWith("{")) {
            return approval.getOperationDetail();
        }
        return approval.getTitle();
    }
}
