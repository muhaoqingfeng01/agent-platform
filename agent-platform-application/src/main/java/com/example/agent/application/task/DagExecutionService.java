package com.example.agent.application.task;

import com.example.agent.application.task.handler.ActionHandler;
import com.example.agent.application.task.handler.ActionHandlerRegistry;
import com.example.agent.application.task.retry.RetryPolicy;
import com.example.agent.application.task.retry.TimeoutController;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.task.entity.TaskExecution;
import com.example.agent.domain.task.entity.TaskStepExecution;
import com.example.agent.domain.task.repository.TaskExecutionRepository;
import com.example.agent.domain.task.repository.TaskStepExecutionRepository;
import com.example.agent.domain.task.valueobject.*;
import com.example.agent.infrastructure.config.websocket.ConversationWebSocketHandler;
import com.example.agent.infrastructure.config.websocket.WebSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DAG 执行器 — Mediator + Facade + Observer 模式.
 *
 * <p>负责按拓扑层级调度 DAG 节点执行、进度推送、失败处理。
 * <p>每层内节点并行执行，层间串行等待。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DagExecutionService {

    private final ActionHandlerRegistry handlerRegistry;
    private final TaskExecutionRepository executionRepository;
    private final TaskStepExecutionRepository stepExecutionRepository;
    private final ConversationWebSocketHandler wsHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 线程池 — JDK 17 下使用固定线程池模拟并行.
     * <p>Java 21+ 可替换为 {@code Executors.newVirtualThreadPerTaskExecutor()}
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Math.max(4, Runtime.getRuntime().availableProcessors() * 2),
            r -> {
                Thread t = new Thread(r, "dag-executor");
                t.setDaemon(true);
                return t;
            });

    /**
     * 异步执行 DAG 任务.
     *
     * @param graph         已解析的 DAG 图
     * @param executionId   执行 ID
     * @param conversationId 会话 ID（用于 WebSocket 推送）
     */
    @Async
    public void execute(DagGraph graph, String executionId, String conversationId) {
        log.info("[DagExec] 开始执行: executionId={}, nodes={}, levels={}",
                executionId, graph.size(),
                graph.getTopologicalLevels() != null ? graph.getTopologicalLevels().size() : 0);

        try {
            // 1. 标记执行开始
            TaskExecution execution = executionRepository.findByExecutionId(executionId)
                    .orElseThrow(() -> new BusinessException(404, "执行记录不存在: " + executionId));
            execution.start();
            executionRepository.update(execution);
            pushProgress(conversationId, executionId, null, "RUNNING", 0, execution.getTotalSteps());

            // 2. 获取拓扑层级
            List<List<TaskNode>> levels = graph.getTopologicalLevels();
            if (levels == null || levels.isEmpty()) {
                log.warn("[DagExec] 拓扑层级为空，无法执行");
                execution.fail(null, "拓扑层级为空");
                executionRepository.markFailed(executionId, null, "拓扑层级为空");
                return;
            }

            // 3. 逐层执行
            Map<String, CompletableFuture<StepResult>> futures = new ConcurrentHashMap<>();
            boolean hasFailed = false;
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

                        if (result.isFailed()) {
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
                pushProgress(conversationId, executionId, null, "RUNNING", completed, execution.getTotalSteps());

                // 3e. 失败时停止后续层
                if (hasFailed) {
                    log.warn("[DagExec] 第 {} 层存在失败步骤，终止后续层级", levelIdx + 1);
                    skipRemainingLevels(levels, levelIdx + 1, executionId, futures);
                    break;
                }
            }

            // 4. 标记最终状态
            TaskExecution finalExec = executionRepository.findByExecutionId(executionId).orElse(null);
            if (finalExec == null) return;

            if (hasFailed) {
                String errMsg = "步骤 " + failedStepId + " 执行失败";
                executionRepository.markFailed(executionId, failedStepId, errMsg);
                pushProgress(conversationId, executionId, null, "FAILED",
                        finalExec.getTotalSteps(), finalExec.getTotalSteps());
            } else {
                finalExec.complete();
                executionRepository.update(finalExec);
                pushProgress(conversationId, executionId, null, "COMPLETED",
                        finalExec.getTotalSteps(), finalExec.getTotalSteps());
            }

            log.info("[DagExec] 执行完成: executionId={}, status={}", executionId,
                    hasFailed ? "FAILED" : "COMPLETED");

        } catch (Exception e) {
            log.error("[DagExec] 执行异常: executionId={}", executionId, e);
            executionRepository.markFailed(executionId, null, "执行异常: " + e.getMessage());
            pushProgress(conversationId, executionId, null, "FAILED", 0, graph.size());
        }
    }

    /**
     * 异步执行单个节点 — 等待依赖完成 → 执行 → 重试 → 超时控制.
     */
    private CompletableFuture<StepResult> executeNodeAsync(
            TaskNode node,
            Map<String, CompletableFuture<StepResult>> futures,
            String executionId,
            String conversationId) {

        // 等待该节点的所有依赖完成
        CompletableFuture<Void> depsFuture;
        if (node.getDep().isEmpty()) {
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

        return depsFuture.thenApplyAsync(v -> {
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
                        node.getParams().put("_upstream_" + depId, depResult.getResult());
                    }
                } catch (Exception e) {
                    return StepResult.skipped(node.getId(),
                            "依赖步骤 " + depId + " 异常: " + e.getMessage());
                }
            }

            // 执行当前节点
            return executeNode(node, executionId, conversationId);
        }, executor);
    }

    /**
     * 执行单个节点 — 含参数校验、超时控制、失败重试、状态持久化.
     */
    private StepResult executeNode(TaskNode node, String executionId, String conversationId) {
        ActionHandler handler = handlerRegistry.getHandler(node.getAction());
        String stepId = node.getId();

        // 1. 更新步骤状态为 RUNNING
        stepExecutionRepository.updateStatus(executionId, stepId, StepStatus.RUNNING);
        pushProgress(conversationId, executionId, stepId, "RUNNING", -1, -1);

        // 2. 参数校验
        try {
            handler.validateParams(node.getParams());
        } catch (IllegalArgumentException e) {
            log.error("[DagExec] 步骤 {} 参数校验失败: {}", stepId, e.getMessage());
            StepResult failResult = StepResult.failed(stepId, "参数校验失败: " + e.getMessage(), 0);
            persistStepResult(executionId, stepId, failResult);
            pushProgress(conversationId, executionId, stepId, "FAILED", -1, -1);
            return failResult;
        }

        // 3. 执行（含超时控制）
        StepResult result = TimeoutController.executeWithTimeout(handler, node.getParams(), stepId);

        // 4. 失败时尝试重试
        if (result.isFailed() && !result.getStatus().equals("TIMEOUT")) {
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
        String wsStatus = result.isSuccess() ? "SUCCESS" : "FAILED";
        pushProgress(conversationId, executionId, stepId, wsStatus, -1, -1);

        return result;
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
        StepStatus status = StepStatus.valueOf(
                result.getStatus().equals("TIMEOUT") ? "FAILED" : result.getStatus());
        stepExecutionRepository.updateResult(executionId, stepId, status,
                outputJson, result.getErrorMessage(), result.getDurationMs());
    }

    /** WebSocket 进度推送 — Observer 模式 */
    private void pushProgress(String conversationId, String executionId,
                               String stepId, String status,
                               int completedSteps, int totalSteps) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("executionId", executionId);
            payload.put("status", status);
            if (stepId != null) payload.put("stepId", stepId);
            if (completedSteps >= 0) payload.put("completedSteps", completedSteps);
            if (totalSteps >= 0) payload.put("totalSteps", totalSteps);

            WebSocketMessage msg = WebSocketMessage.builder()
                    .type("task_progress")
                    .payload(payload)
                    .timestamp(System.currentTimeMillis())
                    .build();

            // 通过 userId 推送（conversationId 映射到 userId 需业务层实现）
            wsHandler.pushMessage(conversationId, msg);
        } catch (Exception e) {
            log.warn("[DagExec] WebSocket 推送失败: executionId={}", executionId, e);
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
        log.info("[DagExec] 任务已取消: executionId={}", executionId);
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
