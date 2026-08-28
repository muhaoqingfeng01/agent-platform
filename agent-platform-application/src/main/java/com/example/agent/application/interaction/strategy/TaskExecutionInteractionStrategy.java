package com.example.agent.application.interaction.strategy;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.task.DagExecutionService;
import com.example.agent.application.task.TaskPlanningService;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.interaction.service.InteractionStrategy;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.domain.task.entity.TaskStepExecution;
import com.example.agent.domain.task.repository.TaskStepExecutionRepository;
import com.example.agent.domain.task.valueobject.ExecutionStatus;
import com.example.agent.domain.task.valueobject.StepStatus;
import com.example.agent.domain.task.valueobject.TaskNode;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行交互策略 — 对话入口规划 DAG 并流式推送步骤进度.
 * <p>
 * 不改动 {@code StreamOrchestrationService}：同步规划、异步执行，
 * 通过 SSE {@code thinking} / {@code task_plan} / {@code task_step} 推进度。
 * 高风险步骤遇到 {@code WAITING_APPROVAL} 时创建审批卡片并结束本轮 stream，
 * 后续 resume 不再占用原 Emitter。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutionInteractionStrategy implements InteractionStrategy {

    private final TaskPlanningService planningService;
    private final DagExecutionService dagExecutionService;
    private final ApprovalWorkflowApplicationService approvalService;
    private final MessageApplicationService messageService;
    private final TaskStepExecutionRepository stepExecutionRepository;
    private final SessionConfig sessionConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public InteractionMode getMode() {
        return InteractionMode.TASK_EXECUTION;
    }

    @Override
    public Object execute(InteractionContext context) {
        throw new BusinessException(400, "任务执行模式请使用流式端点");
    }

    @Override
    public void executeStream(InteractionContext context) {
        Long tenantId = context.getTenantId();
        String userId = context.getUserId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        SseEmitter emitter = (SseEmitter) context.getEmitter();
        if (emitter == null) {
            throw new IllegalStateException("任务执行流式模式需要 SseEmitter，但上下文中未提供");
        }
        String conversationId = context.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalStateException("任务执行流式模式需要 conversationId，但上下文中未提供");
        }
        String userInput = context.getUserInput();

        log.info("[TaskExecutionStrategy] 开始: convId={}, userId={}, inputLength={}",
                conversationId, userId, userInput != null ? userInput.length() : 0);

        ScheduledExecutorService heartbeatExecutor = null;
        String executionId = null;
        try {
            messageService.saveUserMessage(conversationId, userInput);
            heartbeatExecutor = startHeartbeat(emitter);

            sendEvent(emitter, SseEventFactory.thinking("正在规划任务..."));

            TaskPlanningService.PlanResult plan = planTask(userInput, conversationId);
            executionId = plan.getExecutionId();

            List<Map<String, Object>> nodes = toPlanNodes(plan);
            sendEvent(emitter, SseEventFactory.taskPlan(executionId, plan.getTotalSteps(), nodes));
            sendEvent(emitter, SseEventFactory.thinking(
                    "规划完成，共 " + plan.getTotalSteps() + " 步，开始执行..."));

            CompletableFuture<ExecutionStatus> future = dagExecutionService.executeAsync(
                    plan.getGraph(), executionId, conversationId,
                    (execId, stepId, action, status, result, completed, total) -> {
                        if (stepId != null) {
                            sendEvent(emitter, SseEventFactory.taskStep(execId, stepId, action, status, result));
                        }
                    });

            ExecutionStatus finalStatus = future.get();
            finishStream(emitter, conversationId, executionId, plan, finalStatus);

        } catch (BusinessException e) {
            log.warn("[TaskExecutionStrategy] 规划或执行失败: convId={}, msg={}", conversationId, e.getMessage());
            abandonPendingExecution(executionId);
            sendErrorAndComplete(emitter, conversationId, e.getMessage(), e.getCode());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[TaskExecutionStrategy] 等待执行被中断: convId={}", conversationId, e);
            abandonPendingExecution(executionId);
            sendErrorAndComplete(emitter, conversationId, "任务执行被中断", 500);
        } catch (Exception e) {
            log.error("[TaskExecutionStrategy] 流式执行异常: convId={}", conversationId, e);
            abandonPendingExecution(executionId);
            String msg = e.getMessage() != null ? e.getMessage() : "任务执行失败";
            sendErrorAndComplete(emitter, conversationId, msg, 500);
        } finally {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
            }
        }
    }

    @Override
    public int getPriority() {
        return 8;
    }

    // ==================== 内部方法 ====================

    private TaskPlanningService.PlanResult planTask(String userInput, String conversationId) {
        TaskPlanningService.PlanRequest request = new TaskPlanningService.PlanRequest();
        request.setUserIntent(userInput);
        request.setConversationId(conversationId);
        return planningService.plan(request);
    }

    private List<Map<String, Object>> toPlanNodes(TaskPlanningService.PlanResult plan) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        if (plan.getGraph() == null || plan.getGraph().getNodes() == null) {
            return nodes;
        }
        for (TaskNode node : plan.getGraph().getNodes()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", node.getId());
            item.put("action", node.getAction());
            item.put("description", node.getDescription());
            item.put("dep", node.getDep() != null ? node.getDep() : List.of());
            nodes.add(item);
        }
        return nodes;
    }

    private void finishStream(SseEmitter emitter, String conversationId, String executionId,
                              TaskPlanningService.PlanResult plan, ExecutionStatus status) {
        if (status == ExecutionStatus.WAITING_APPROVAL) {
            handleWaitingApproval(emitter, conversationId, executionId, plan);
            return;
        }
        if (status == ExecutionStatus.FAILED) {
            sendErrorAndComplete(emitter, conversationId, "任务执行失败", 500);
            return;
        }
        if (status == ExecutionStatus.CANCELLED) {
            sendErrorAndComplete(emitter, conversationId, "任务已取消", 409);
            return;
        }

        String summary = "任务执行完成，共 " + plan.getTotalSteps() + " 步。";
        sendEvent(emitter, SseEventFactory.token(summary));
        Message assistantMsg = messageService.saveAssistantMessage(
                conversationId, summary, estimateTokenCount(summary));
        sendEvent(emitter, SseEventFactory.done(estimateTokenCount(summary), assistantMsg.getMessageId()));
        emitter.complete();
    }

    private void handleWaitingApproval(SseEmitter emitter, String conversationId,
                                       String executionId, TaskPlanningService.PlanResult plan) {
        TaskStepExecution waiting = stepExecutionRepository.findByExecutionId(executionId).stream()
                .filter(s -> s.getStatus() == StepStatus.WAITING_APPROVAL)
                .findFirst()
                .orElse(null);

        String action = waiting != null ? waiting.getAction() : "high_risk_action";
        String stepId = waiting != null ? waiting.getStepId() : "unknown";
        Map<String, Object> params = parseParams(waiting != null ? waiting.getInputJson() : null);

        ApprovalWorkflowResponse approval = approvalService.createActionApproval(
                executionId, conversationId, action, stepId, params);

        sendEvent(emitter, SseEventFactory.approvalRequired(
                approval.getApprovalId(), executionId, approval.getTitle()));
        sendEvent(emitter, SseEventFactory.thinking("高风险步骤需审批，本轮对话已结束，通过后将自动继续。"));

        String summary = "任务已暂停，等待审批通过后继续执行（共 " + plan.getTotalSteps() + " 步）。";
        sendEvent(emitter, SseEventFactory.token(summary));
        Message assistantMsg = messageService.saveAssistantMessage(
                conversationId, summary, estimateTokenCount(summary));
        sendEvent(emitter, SseEventFactory.done(estimateTokenCount(summary), assistantMsg.getMessageId()));
        emitter.complete();

        log.info("[TaskExecutionStrategy] 本轮 stream 因审批结束: executionId={}, approvalId={}",
                executionId, approval.getApprovalId());
    }

    /**
     * 规划已成功但后续失败时，取消仍为 PENDING 的执行，避免留下 FAILED 脏数据.
     * 规划阶段抛错时 executionId 为空，且 {@code plan()} 事务会回滚，不会落库.
     */
    private void abandonPendingExecution(String executionId) {
        if (executionId == null || executionId.isBlank()) {
            return;
        }
        try {
            dagExecutionService.cancel(executionId);
        } catch (Exception e) {
            log.warn("[TaskExecutionStrategy] 清理未完成执行失败: executionId={}", executionId, e);
        }
    }

    private void sendErrorAndComplete(SseEmitter emitter, String conversationId, String message, int code) {
        sendEvent(emitter, SseEventFactory.error(message, code));
        try {
            String content = "任务执行失败: " + message;
            messageService.saveAssistantMessage(conversationId, content, estimateTokenCount(content));
        } catch (Exception e) {
            log.warn("[TaskExecutionStrategy] 保存失败消息异常", e);
        }
        try {
            emitter.complete();
        } catch (Exception ignored) {
            // 连接已关闭
        }
    }

    private ScheduledExecutorService startHeartbeat(SseEmitter emitter) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "sse-task-heartbeat"));
        long intervalMs = sessionConfig != null
                ? sessionConfig.getSseHeartbeatIntervalMs() : 15_000L;
        executor.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data(""));
            } catch (Exception ignored) {
                // 连接已关闭
            }
        }, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
        return executor;
    }

    private void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (Exception e) {
            log.warn("[TaskExecutionStrategy] SSE 发送失败", e);
        }
    }

    private Map<String, Object> parseParams(String inputJson) {
        if (inputJson == null || inputJson.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(inputJson, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private int estimateTokenCount(String content) {
        if (content == null) {
            return 0;
        }
        return (int) Math.ceil(content.length() * 0.5);
    }
}
