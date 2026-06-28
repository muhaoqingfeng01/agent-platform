package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.task.DagExecutionService;
import com.example.agent.application.task.TaskPlanningService;
import com.example.agent.application.task.handler.ActionHandlerRegistry;
import com.example.agent.common.result.Result;
import com.example.agent.domain.task.entity.TaskExecution;
import com.example.agent.domain.task.entity.TaskStepExecution;
import com.example.agent.domain.task.repository.TaskExecutionRepository;
import com.example.agent.domain.task.repository.TaskStepExecutionRepository;
import com.example.agent.domain.task.service.DagParser;
import com.example.agent.interfaces.dto.request.task.TaskCreatePlanRequest;
import com.example.agent.interfaces.dto.request.task.TaskExecuteRequest;
import com.example.agent.interfaces.dto.request.task.TaskCancelRequest;
import com.example.agent.interfaces.dto.request.task.TaskExecutionGetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务规划与执行 Controller — 纯粹 HTTP 适配层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "任务规划与执行", description = "LLM 任务规划、DAG 执行、进度查询、取消")
public class TaskController {

    private final TaskPlanningService planningService;
    private final DagExecutionService executionService;
    private final TaskExecutionRepository executionRepository;
    private final TaskStepExecutionRepository stepExecutionRepository;
    private final DagParser dagParser;
    private final ActionHandlerRegistry handlerRegistry;

    @PostMapping("/plan")
    @SaCheckPermission("task:create")
    @Operation(summary = "规划任务", description = "调用 LLM 将用户意图拆解为 DAG 步骤序列")
    public Result<Map<String, Object>> plan(@Valid @RequestBody TaskCreatePlanRequest request) {
        TaskPlanningService.PlanRequest planReq = new TaskPlanningService.PlanRequest();
        planReq.setUserIntent(request.getUserIntent());
        planReq.setConversationId(request.getConversationId());
        planReq.setAgentId(request.getAgentId());
        TaskPlanningService.PlanResult result = planningService.plan(planReq);
        return Result.ok(Map.of(
                "executionId", result.getExecutionId(),
                "totalSteps", result.getTotalSteps(),
                "levels", result.getLevels(),
                "message", "任务规划完成，共 " + result.getTotalSteps() + " 步，分 " + result.getLevels() + " 层执行"
        ));
    }

    @PostMapping("/execute")
    @SaCheckPermission("task:execute")
    @Operation(summary = "执行任务", description = "根据已规划的执行计划启动 DAG 并行执行")
    public Result<Map<String, String>> execute(@Valid @RequestBody TaskExecuteRequest request) {
        TaskExecution execution = executionRepository.findByExecutionId(request.getExecutionId())
                .orElseThrow(() -> new com.example.agent.common.exception.BusinessException(404,
                        "执行记录不存在: " + request.getExecutionId()));
        var graph = dagParser.parse(execution.getPlanJson());
        if (request.isAsync()) {
            executionService.execute(graph, request.getExecutionId(), execution.getConversationId());
            return Result.ok(Map.of(
                    "executionId", request.getExecutionId(),
                    "status", "RUNNING",
                    "message", "任务已提交异步执行"
            ));
        } else {
            executionService.execute(graph, request.getExecutionId(), execution.getConversationId());
            TaskExecution updated = executionRepository.findByExecutionId(request.getExecutionId()).orElse(execution);
            return Result.ok(Map.of(
                    "executionId", request.getExecutionId(),
                    "status", updated.getStatus().name()
            ));
        }
    }

    @PostMapping("/status")
    @SaCheckPermission("task:read")
    @Operation(summary = "查询执行状态", description = "获取任务执行的当前进度和各步骤状态")
    public Result<DagExecutionService.ExecutionStatusResponse> status(@Valid @RequestBody TaskExecutionGetRequest request) {
        TaskExecution execution = executionRepository.findByExecutionId(request.getExecutionId())
                .orElseThrow(() -> new com.example.agent.common.exception.BusinessException(404,
                        "执行记录不存在: " + request.getExecutionId()));
        List<TaskStepExecution> steps = stepExecutionRepository.findByExecutionId(request.getExecutionId());
        return Result.ok(DagExecutionService.ExecutionStatusResponse.from(execution, steps));
    }

    @PostMapping("/plan/get")
    @SaCheckPermission("task:read")
    @Operation(summary = "查询执行计划", description = "获取已规划的任务 DAG 结构")
    public Result<Map<String, Object>> getPlan(@Valid @RequestBody TaskExecutionGetRequest request) {
        TaskPlanningService.PlanResult result = planningService.getPlan(request.getExecutionId());
        return Result.ok(Map.of(
                "executionId", result.getExecutionId(),
                "totalSteps", result.getTotalSteps(),
                "levels", result.getLevels(),
                "nodes", result.getGraph().getNodes()
        ));
    }

    @PostMapping("/cancel")
    @SaCheckPermission("task:execute")
    @Operation(summary = "取消执行", description = "取消正在执行或等待中的任务")
    public Result<Map<String, String>> cancel(@Valid @RequestBody TaskCancelRequest request) {
        executionService.cancel(request.getExecutionId());
        return Result.ok(Map.of(
                "executionId", request.getExecutionId(),
                "status", "CANCELLED",
                "message", "任务已取消"
        ));
    }

    @PostMapping("/handlers")
    @SaCheckPermission("task:read")
    @Operation(summary = "可用动作列表", description = "获取所有已注册的 ActionHandler 及其参数 Schema")
    public Result<List<Map<String, Object>>> listHandlers() {
        List<Map<String, Object>> handlers = handlerRegistry.getAllHandlers().stream()
                .map(h -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("action", h.getActionType());
                    info.put("description", h.getDescription());
                    info.put("paramsSchema", h.getParamsSchema());
                    info.put("highRisk", h.isHighRisk());
                    info.put("maxRetries", h.maxRetries());
                    info.put("timeoutMs", h.timeoutMs());
                    return info;
                })
                .toList();
        return Result.ok(handlers);
    }
}
