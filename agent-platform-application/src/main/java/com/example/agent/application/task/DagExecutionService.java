package com.example.agent.application.task;

import com.example.agent.application.task.handler.ActionHandler;
import com.example.agent.application.task.handler.ActionHandlerRegistry;
import com.example.agent.application.task.retry.RetryPolicy;
import com.example.agent.application.task.retry.TimeoutController;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.util.MdcContext;
import com.example.agent.domain.task.entity.TaskExecution;
import com.example.agent.domain.task.entity.TaskStepExecution;
import com.example.agent.domain.task.repository.TaskExecutionRepository;
import com.example.agent.domain.task.repository.TaskStepExecutionRepository;
import com.example.agent.domain.task.service.DagParser;
import com.example.agent.domain.task.valueobject.*;
import com.example.agent.infrastructure.config.websocket.ConversationWebSocketHandler;
import com.example.agent.infrastructure.config.websocket.WebSocketMessage;
import com.example.agent.infrastructure.config.websocket.WebSocketMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DAG 执行器 — Mediator + Facade + Observer 模式.
 *
 * <p>负责按拓扑层级调度 DAG 节点执行、进度推送、失败处理。
 * <p>每层内节点并行执行，层间串行等待。
 * <p>MDC 上下文（traceId 等）通过 {@link MdcContext} 显式传播，
 * 因为 Spring 6.1 的 {@code ThreadPoolTaskExecutor.execute/submit} 不经过 {@code TaskDecorator}。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class DagExecutionService {

    private final ActionHandlerRegistry handlerRegistry;
    private final TaskExecutionRepository executionRepository;
    private final TaskStepExecutionRepository stepExecutionRepository;
    private final ConversationWebSocketHandler wsHandler;
    private final ThreadPoolTaskExecutor asyncTaskExecutor;
    private final DagParser dagParser;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 按 executionId 注册的 SSE/调用方进度监听器 */
    private final ConcurrentHashMap<String, DagProgressListener> progressListeners = new ConcurrentHashMap<>();

    /** 按 executionId 注册的执行完成 Future，供交互策略等待终态或审批暂停 */
    private final ConcurrentHashMap<String, CompletableFuture<ExecutionStatus>> completionFutures = new ConcurrentHashMap<>();

    /**
     * DAG 节点并行执行线程池 — JDK 17 下使用固定线程池模拟并行.
     * <p>Java 21+ 可替换为 {@code Executors.newVirtualThreadPerTaskExecutor()}
     */
    private final ExecutorService nodeExecutor = Executors.newFixedThreadPool(
            Math.max(4, Runtime.getRuntime().availableProcessors() * 2),
            r -> {
                Thread t = new Thread(r, "dag-executor");
                t.setDaemon(true);
                return t;
            });

    public DagExecutionService(ActionHandlerRegistry handlerRegistry,
                               TaskExecutionRepository executionRepository,
                               TaskStepExecutionRepository stepExecutionRepository,
                               ConversationWebSocketHandler wsHandler,
                               @Qualifier("asyncTaskExecutor") ThreadPoolTaskExecutor asyncTaskExecutor,
                               DagParser dagParser) {
        this.handlerRegistry = handlerRegistry;
        this.executionRepository = executionRepository;
        this.stepExecutionRepository = stepExecutionRepository;
        this.wsHandler = wsHandler;
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.dagParser = dagParser;
    }

    /**
     * 异步执行 DAG 任务 — 通过 {@link MdcContext#wrap(Runnable)} 传播 MDC.
     *
     * @param graph          已解析的 DAG 图
     * @param executionId    执行 ID
     * @param conversationId 会话 ID（用于 WebSocket 推送）
     */
    public void execute(DagGraph graph, String executionId, String conversationId) {
        executeAsync(graph, executionId, conversationId, null);
    }

    /**
     * 异步执行 DAG，并返回可等待的完成 Future.
     * <p>终态为 COMPLETED / FAILED / CANCELLED，高风险暂停时完成值为 WAITING_APPROVAL.
     *
     * @param graph          已解析的 DAG 图
     * @param executionId    执行 ID
     * @param conversationId 会话 ID（用于 WebSocket 推送）
     * @param listener       可选进度监听器（SSE task_step）
     * @return 执行结束时的状态 Future
     */
    public CompletableFuture<ExecutionStatus> executeAsync(DagGraph graph, String executionId,
                                                            String conversationId,
                                                            DagProgressListener listener) {
        CompletableFuture<ExecutionStatus> future = new CompletableFuture<>();
        completionFutures.put(executionId, future);
        if (listener != null) {
            progressListeners.put(executionId, listener);
        }
        asyncTaskExecutor.submit(MdcContext.wrap(() -> {
            try {
                executeInternal(graph, executionId, conversationId);
            } catch (Exception e) {
                log.error("[DagExec] 异步执行未捕获异常: executionId={}", executionId, e);
                completeExecution(executionId, ExecutionStatus.FAILED);
            }
        }));
        return future;
    }

    /**
     * 内部执行逻辑 — 运行在 asyncTaskExecutor 线程上，MDC 已由 MdcContext.wrap() 恢复.
     */
    private void executeInternal(DagGraph graph, String executionId, String conversationId) {
        log.info("[DagExec] 开始执行: executionId={}, nodes={}, levels={}",
                executionId, graph.size(),
                graph.getTopologicalLevels() != null ? graph.getTopologicalLevels().size() : 0);

        try {
            // 1. 标记执行开始（首次 PENDING）或接续执行（审批恢复后已是 RUNNING）
            TaskExecution execution = executionRepository.findByExecutionId(executionId)
                    .orElseThrow(() -> new BusinessException(404, "执行记录不存在: " + executionId));
            if (execution.getStatus() == ExecutionStatus.PENDING) {
                execution.start();
                executionRepository.update(execution);
            } else if (execution.getStatus() != ExecutionStatus.RUNNING) {
                log.warn("[DagExec] 当前状态不可执行: executionId={}, status={}",
                        executionId, execution.getStatus());
                completeExecution(executionId, execution.getStatus());
                return;
            }
            pushProgress(conversationId, executionId, null, null, ExecutionStatus.RUNNING.name(),
                    0, execution.getTotalSteps(), null);

            // 2. 获取拓扑层级
            List<List<TaskNode>> levels = graph.getTopologicalLevels();
            if (levels == null || levels.isEmpty()) {
                log.warn("[DagExec] 拓扑层级为空，无法执行");
                execution.fail(null, "拓扑层级为空");
                executionRepository.markFailed(executionId, null, "拓扑层级为空");
                completeExecution(executionId, ExecutionStatus.FAILED);
                return;
            }

            // 3. 逐层执行
            Map<String, CompletableFuture<StepResult>> futures = new ConcurrentHashMap<>();
            boolean hasFailed = false;
            boolean hasWaitingApproval = false;
            String failedStepId = null;

            for (int levelIdx = 0; levelIdx < levels.size(); levelIdx++) {
                List<TaskNode> level = levels.get(levelIdx);
                log.info("[DagExec] 执行第 {} 层: {} 个节点", levelIdx + 1, level.size());

                // 3a. 当前层每个节点等待依赖完成
                List<CompletableFuture<StepResult>> levelFutures = level.stream()
                        .map(node -> executeNodeAsync(node, futures, executionId, conversationId))
                        .toList();

                // 3b. 等待当前层全部完成
                CompletableFuture.allOf(levelFutures.toArray(new CompletableFuture[0])).join();

                // 3c. 收集结果，检测失败
                for (int i = 0; i < level.size(); i++) {
                    TaskNode node = level.get(i);
                    try {
                        StepResult result = levelFutures.get(i).get();
                        futures.put(node.getId(), CompletableFuture.completedFuture(result));

                        if (result.isWaitingApproval()) {
                            hasWaitingApproval = true;
                            log.info("[DagExec] 步骤 {} 等待审批，暂停后续层级", node.getId());
                        } else if (result.isFailed()) {
                            hasFailed = true;
                            failedStepId = node.getId();
                            log.error("[DagExec] 步骤 {} 执行失败: {}", node.getId(), result.getErrorMessage());
                        } else {
                            log.info("[DagExec] 步骤 {} 执行成功 ({}ms)", node.getId(), result.getDurationMs());
                        }
                    } catch (Exception e) {
                        hasFailed = true;
                        failedStepId = node.getId();
                        futures.put(node.getId(), CompletableFuture.failedFuture(e));
                        log.error("[DagExec] 步骤 {} 异常", node.getId(), e);
                    }
                }

                // 3d. 更新进度
                int completed = (int) futures.values().stream()
                        .filter(f -> {
                            try {
                                return !f.isCompletedExceptionally() && f.get().isSuccess();
                            } catch (Exception e) {
                                return false;
                            }
                        }).count();
                executionRepository.updateProgress(executionId, completed);
                pushProgress(conversationId, executionId, null, null, ExecutionStatus.RUNNING.name(),
                        completed, execution.getTotalSteps(), null);

                // 3e. 等待审批：保留后续 PENDING 步骤，供审批通过后 resume
                if (hasWaitingApproval) {
                    log.info("[DagExec] 遇到 WAITING_APPROVAL，暂停后续层级: executionId={}", executionId);
                    break;
                }

                // 3f. 失败时停止后续层
                if (hasFailed) {
                    log.warn("[DagExec] 第 {} 层存在失败步骤，终止后续层级", levelIdx + 1);
                    skipRemainingLevels(levels, levelIdx + 1, executionId, futures);
                    break;
                }
            }

            // 4. 标记最终状态
            TaskExecution finalExec = executionRepository.findByExecutionId(executionId).orElse(null);
            if (finalExec == null) {
                completeExecution(executionId, ExecutionStatus.FAILED);
                return;
            }

            if (hasWaitingApproval) {
                pushProgress(conversationId, executionId, null, null, ExecutionStatus.WAITING_APPROVAL.name(),
                        finalExec.getCompletedSteps(), finalExec.getTotalSteps(), null);
                completeExecution(executionId, ExecutionStatus.WAITING_APPROVAL);
                log.info("[DagExec] 执行暂停等待审批: executionId={}", executionId);
                return;
            }

            if (hasFailed) {
                String errMsg = "步骤 " + failedStepId + " 执行失败";
                executionRepository.markFailed(executionId, failedStepId, errMsg);
                pushProgress(conversationId, executionId, null, null, ExecutionStatus.FAILED.name(),
                        finalExec.getTotalSteps(), finalExec.getTotalSteps(), null);
                completeExecution(executionId, ExecutionStatus.FAILED);
            } else {
                finalExec.complete();
                executionRepository.update(finalExec);
                pushProgress(conversationId, executionId, null, null, ExecutionStatus.COMPLETED.name(),
                        finalExec.getTotalSteps(), finalExec.getTotalSteps(), null);
                completeExecution(executionId, ExecutionStatus.COMPLETED);
            }

            log.info("[DagExec] 执行完成: executionId={}, status={}", executionId,
                    hasFailed ? ExecutionStatus.FAILED : ExecutionStatus.COMPLETED);

        } catch (Exception e) {
            log.error("[DagExec] 执行异常: executionId={}", executionId, e);
            try {
                executionRepository.markFailed(executionId, null, "执行异常: " + e.getMessage());
            } catch (Exception dbEx) {
                log.error("[DagExec] 标记失败状态时数据库异常: executionId={}", executionId, dbEx);
            }
            pushProgress(conversationId, executionId, null, null, ExecutionStatus.FAILED.name(), 0, graph.size(), null);
            completeExecution(executionId, ExecutionStatus.FAILED);
            throw new BusinessException(500, "DAG 执行异常: " + executionId, e);
        }
    }

    /**
     * 异步执行单个节点 — 等待依赖完成 → 执行 → 重试 → 超时控制.
     * <p>MDC 通过 {@link MdcContext#wrapFn(java.util.function.Function)} 传播到节点线程.
     */
    private CompletableFuture<StepResult> executeNodeAsync(
            TaskNode node,
            Map<String, CompletableFuture<StepResult>> futures,
            String executionId,
            String conversationId) {

        // 等待该节点的所有依赖完成
        CompletableFuture<Void> depsFuture;
        if (node.getDep() == null || node.getDep().isEmpty()) {
            depsFuture = CompletableFuture.completedFuture(null);
        } else {
            CompletableFuture<?>[] depFutures = node.getDep().stream()
                    .map(depId -> {
                        CompletableFuture<StepResult> f = futures.get(depId);
                        if (f == null) {
                            return CompletableFuture.<StepResult>failedFuture(
                                    new IllegalStateException("依赖步骤 " + depId + " 未找到"));
                        }
                        return f;
                    })
                    .toArray(CompletableFuture[]::new);
            depsFuture = CompletableFuture.allOf(depFutures);
        }

        // MdcContext.wrapFn() 在调用线程（asyncTaskExecutor 线程）上捕获 MDC，
        // 在节点执行线程（dag-executor 线程）上恢复 MDC
        return depsFuture.thenApplyAsync(MdcContext.wrapFn(v -> {
            // 检查依赖是否全部成功
            for (String depId : node.getDep()) {
                try {
                    StepResult depResult = futures.get(depId).get();
                    if (depResult.isFailed()) {
                        return StepResult.skipped(node.getId(),
                                "依赖步骤 " + depId + " 失败，跳过本步骤");
                    }
                    // 将上游步骤结果注入当前步骤参数
                    if (depResult.getResult() != null) {
                        if (node.getParams() == null) {
                            node.setParams(new java.util.HashMap<>());
                        }
                        node.getParams().put("_upstream_" + depId, depResult.getResult());
                    }
                } catch (Exception e) {
                    return StepResult.skipped(node.getId(),
                            "依赖步骤 " + depId + " 异常: " + e.getMessage());
                }
            }

            // 执行当前节点
            return executeNode(node, executionId, conversationId);
        }), nodeExecutor);
    }

    /**
     * 执行单个节点 — 含参数校验、超时控制、失败重试、高风险审批暂停、状态持久化.
     */
    private StepResult executeNode(TaskNode node, String executionId, String conversationId) {
        ActionHandler handler = handlerRegistry.getHandler(node.getAction());
        String stepId = node.getId();
        String action = node.getAction();
        if (node.getParams() == null) {
            node.setParams(new java.util.HashMap<>());
        }

        TaskStepExecution existing = stepExecutionRepository
                .findByExecutionIdAndStepId(executionId, stepId)
                .orElse(null);

        // 审批恢复：已成功步骤直接复用结果，避免重跑
        if (existing != null && existing.getStatus() == StepStatus.SUCCESS) {
            return StepResult.success(stepId, parseOutput(existing.getOutputJson()),
                    existing.getDurationMs() != null ? existing.getDurationMs() : 0);
        }

        // 审批恢复：当前步骤上次停在 WAITING_APPROVAL，本次直接执行（不再二次审批）
        boolean resumeApprovedStep = existing != null && existing.getStatus() == StepStatus.WAITING_APPROVAL;

        // 1. 更新步骤状态为 RUNNING
        stepExecutionRepository.updateStatus(executionId, stepId, StepStatus.RUNNING);
        pushProgress(conversationId, executionId, stepId, action, StepStatus.RUNNING.name(), -1, -1, null);

        // 2. 参数校验
        try {
            handler.validateParams(node.getParams());
        } catch (IllegalArgumentException e) {
            log.error("[DagExec] 步骤 {} 参数校验失败: {}", stepId, e.getMessage());
            StepResult failResult = StepResult.failed(stepId, "参数校验失败: " + e.getMessage(), 0);
            persistStepResult(executionId, stepId, failResult);
            pushProgress(conversationId, executionId, stepId, action, StepStatus.FAILED.name(), -1, -1, null);
            return failResult;
        }

        // 2.1 高风险步骤：暂停等待审批（B6）。恢复执行时跳过本检查。
        if (handler.isHighRisk() && !resumeApprovedStep) {
            pauseForApproval(executionId, stepId, action, conversationId);
            return StepResult.waitingApproval(stepId);
        }

        // 3. 执行（含超时控制）
        StepResult result = TimeoutController.executeWithTimeout(handler, node.getParams(), stepId);

        // 4. 失败时尝试重试（超时不重试）
        if (result.isFailed() && result.getStatus() != StepStatus.TIMEOUT) {
            int maxRetries = handler.maxRetries();
            if (maxRetries > 0) {
                log.info("[DagExec] 步骤 {} 失败，开始重试 (max={})", stepId, maxRetries);
                // 更新重试状态
                stepExecutionRepository.updateRetry(executionId, stepId, 1, StepStatus.RUNNING);
                result = RetryPolicy.executeWithRetry(handler, node.getParams(), maxRetries, stepId);
                // 持久化最终重试次数
                if (result.isRetried()) {
                    stepExecutionRepository.updateRetry(executionId, stepId,
                            result.getRetryCount(),
                            result.isSuccess() ? StepStatus.SUCCESS : StepStatus.FAILED);
                }
            }
        }

        // 5. 持久化步骤结果
        persistStepResult(executionId, stepId, result);

        // 6. 推送进度
        pushProgress(conversationId, executionId, stepId, action, result.getStatus().name(),
                -1, -1, result.getResult());

        return result;
    }

    /** 高风险步骤：将执行与步骤置为 WAITING_APPROVAL */
    private void pauseForApproval(String executionId, String stepId, String action, String conversationId) {
        TaskExecution execution = executionRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new BusinessException(404, "执行记录不存在: " + executionId));
        if (execution.getStatus() == ExecutionStatus.RUNNING) {
            try {
                execution.waitForApproval();
                executionRepository.update(execution);
            } catch (IllegalStateException e) {
                log.info("[DagExec] 执行已处于审批等待: executionId={}, msg={}", executionId, e.getMessage());
            }
        }
        stepExecutionRepository.updateStatus(executionId, stepId, StepStatus.WAITING_APPROVAL);
        pushProgress(conversationId, executionId, stepId, action, StepStatus.WAITING_APPROVAL.name(),
                -1, -1, null);
        log.info("[DagExec] 高风险步骤暂停等待审批: executionId={}, stepId={}, action={}",
                executionId, stepId, action);
    }

    private Object parseOutput(String outputJson) {
        if (outputJson == null || outputJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(outputJson, Object.class);
        } catch (JsonProcessingException e) {
            return outputJson;
        }
    }

    /**
     * 跳过剩余层级的所有步骤（前置步骤失败导致）.
     */
    private void skipRemainingLevels(List<List<TaskNode>> levels, int fromLevel,
                                      String executionId,
                                      Map<String, CompletableFuture<StepResult>> futures) {
        for (int i = fromLevel; i < levels.size(); i++) {
            for (TaskNode node : levels.get(i)) {
                stepExecutionRepository.updateStatus(executionId, node.getId(), StepStatus.SKIPPED);
                futures.put(node.getId(),
                        CompletableFuture.completedFuture(
                                StepResult.skipped(node.getId(), "前置步骤失败，跳过本步骤")));
            }
        }
    }

    /** 持久化步骤执行结果 */
    private void persistStepResult(String executionId, String stepId, StepResult result) {
        String outputJson = null;
        if (result.getResult() != null) {
            try {
                outputJson = objectMapper.writeValueAsString(result.getResult());
            } catch (JsonProcessingException e) {
                outputJson = "{\"error\":\"序列化失败\"}";
            }
        }
        StepStatus status = result.getStatus();
        stepExecutionRepository.updateResult(executionId, stepId, status,
                outputJson, result.getErrorMessage(), result.getDurationMs());
    }

    /** WebSocket + 可选 SSE 进度推送 — Observer 模式 */
    private void pushProgress(String conversationId, String executionId,
                               String stepId, String action, String status,
                               int completedSteps, int totalSteps, Object result) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("executionId", executionId);
            payload.put("status", status);
            if (stepId != null) payload.put("stepId", stepId);
            if (action != null) payload.put("action", action);
            if (completedSteps >= 0) payload.put("completedSteps", completedSteps);
            if (totalSteps >= 0) payload.put("totalSteps", totalSteps);

            WebSocketMessage msg = WebSocketMessage.builder()
                    .type(WebSocketMessageType.TASK_PROGRESS)
                    .payload(payload)
                    .timestamp(System.currentTimeMillis())
                    .build();

            wsHandler.pushMessage(conversationId, msg);
        } catch (Exception e) {
            log.warn("[DagExec] WebSocket 推送失败: executionId={}", executionId, e);
        }

        DagProgressListener listener = progressListeners.get(executionId);
        if (listener != null) {
            try {
                listener.onProgress(executionId, stepId, action, status, result, completedSteps, totalSteps);
            } catch (Exception e) {
                log.warn("[DagExec] 进度监听器异常: executionId={}", executionId, e);
            }
        }
    }

    /** 完成等待方 Future 并清理监听器 */
    private void completeExecution(String executionId, ExecutionStatus status) {
        progressListeners.remove(executionId);
        CompletableFuture<ExecutionStatus> future = completionFutures.remove(executionId);
        if (future != null && !future.isDone()) {
            future.complete(status);
        }
    }

    /**
     * 取消正在执行的任务.
     *
     * @param executionId 执行 ID
     */
    @Transactional
    public void cancel(String executionId) {
        TaskExecution execution = executionRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new BusinessException(404, "执行记录不存在: " + executionId));
        execution.cancel();
        executionRepository.update(execution);
        stepExecutionRepository.batchUpdateStatusByExecutionId(executionId, StepStatus.SKIPPED);
        completeExecution(executionId, ExecutionStatus.CANCELLED);
        log.info("[DagExec] 任务已取消: executionId={}", executionId);
    }

    /**
     * 取消执行并记录原因（审批拒绝/超时时调用）.
     *
     * @param executionId 执行 ID
     * @param reason      取消原因
     */
    @Transactional
    public void cancelExecution(String executionId, String reason) {
        TaskExecution execution = executionRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new BusinessException(404, "执行记录不存在: " + executionId));

        if (!execution.getStatus().isActive()) {
            throw new BusinessException(409, "任务无法取消，当前状态: " + execution.getStatus());
        }

        execution.cancel(reason);
        executionRepository.update(execution);
        stepExecutionRepository.batchUpdateStatusByExecutionId(executionId, StepStatus.SKIPPED);

        log.info("[DagExec] 任务已取消: executionId={}, reason={}", executionId, reason);

        // WebSocket 推送取消通知
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("executionId", executionId);
        payload.put("status", ExecutionStatus.CANCELLED.name());
        payload.put("reason", reason);
        WebSocketMessage msg = WebSocketMessage.builder()
                .type(WebSocketMessageType.TASK_CANCELLED)
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
        wsHandler.pushMessage(execution.getConversationId(), msg);
        completeExecution(executionId, ExecutionStatus.CANCELLED);
    }

    /**
     * 恢复执行（审批通过后调用）.
     *
     * <p>将任务从 WAITING_APPROVAL 状态恢复为 RUNNING，并重新触发 DAG 执行。
     *
     * @param executionId 执行 ID
     */
    @Transactional
    public void resumeExecution(String executionId) {
        TaskExecution execution = executionRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new BusinessException(404, "执行记录不存在: " + executionId));

        if (execution.getStatus() != ExecutionStatus.WAITING_APPROVAL) {
            throw new BusinessException(409,
                    "只有 WAITING_APPROVAL 状态的任务才能恢复，当前: " + execution.getStatus());
        }

        // 恢复为 RUNNING
        execution.resumeFromApproval();
        executionRepository.update(execution);

        log.info("[DagExec] 任务恢复执行: executionId={}", executionId);

        // 用 DagParser 解析节点列表 JSON（planJson 不是 DagGraph 对象）
        try {
            DagGraph graph = dagParser.parse(execution.getPlanJson());
            String conversationId = execution.getConversationId();
            this.execute(graph, executionId, conversationId);
        } catch (Exception e) {
            log.error("[DagExec] 解析 DAG 计划失败: executionId={}", executionId, e);
            execution.fail(null, "恢复执行失败: 计划解析异常");
            executionRepository.markFailed(executionId, null, "恢复执行失败: " + e.getMessage());
        }
    }

    /**
     * DAG 执行进度监听器 — 供交互策略将步骤进度转为 SSE task_step.
     */
    @FunctionalInterface
    public interface DagProgressListener {
        void onProgress(String executionId, String stepId, String action, String status,
                        Object result, int completedSteps, int totalSteps);
    }

    // ==================== DTOs ====================

    @Data
    @Builder
    public static class ExecutionStatusResponse {
        private String executionId;
        private String status;
        private int totalSteps;
        private int completedSteps;
        private String failedStepId;
        private String errorMessage;
        private List<StepStatusResponse> steps;

        public static ExecutionStatusResponse from(TaskExecution execution,
                                                    List<TaskStepExecution> steps) {
            return ExecutionStatusResponse.builder()
                    .executionId(execution.getExecutionId())
                    .status(execution.getStatus().name())
                    .totalSteps(execution.getTotalSteps())
                    .completedSteps(execution.getCompletedSteps())
                    .failedStepId(execution.getFailedStepId())
                    .errorMessage(execution.getErrorMessage())
                    .steps(steps.stream().map(StepStatusResponse::from).toList())
                    .build();
        }
    }

    @Data
    @Builder
    public static class StepStatusResponse {
        private String stepId;
        private String action;
        private String status;
        private int retryCount;
        private Long durationMs;
        private String errorMessage;

        public static StepStatusResponse from(TaskStepExecution step) {
            return StepStatusResponse.builder()
                    .stepId(step.getStepId())
                    .action(step.getAction())
                    .status(step.getStatus().name())
                    .retryCount(step.getRetryCount())
                    .durationMs(step.getDurationMs())
                    .errorMessage(step.getErrorMessage())
                    .build();
        }
    }
}
