package com.example.agent.application.task;

import com.example.agent.application.task.handler.ActionHandler;
import com.example.agent.application.task.handler.ActionHandlerRegistry;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.task.entity.TaskExecution;
import com.example.agent.domain.task.entity.TaskStepExecution;
import com.example.agent.domain.task.repository.TaskExecutionRepository;
import com.example.agent.domain.task.repository.TaskStepExecutionRepository;
import com.example.agent.domain.task.service.DagParser;
import com.example.agent.domain.task.valueobject.*;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务规划应用服务 — Strategy + Template Method 模式.
 *
 * <p>负责调用 LLM 进行任务拆解、DAG 解析、执行计划持久化。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPlanningService {

    private final ChatClient chatClient;
    private final DagParser dagParser;
    private final ActionHandlerRegistry handlerRegistry;
    private final TaskExecutionRepository executionRepository;
    private final TaskStepExecutionRepository stepExecutionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== LLM 规划提示词 ====================

    private static final String PLANNING_SYSTEM_PROMPT = """
            你是一个任务规划器。根据用户意图，将复杂任务拆解为可执行的步骤序列。

            ## 可用动作
            %s

            ## 输出格式
            返回 JSON 数组，每个元素包含：
            - "id": 步骤唯一标识（字符串，如 "1", "2", "3"）
            - "action": 动作类型（从可用动作中选择）
            - "description": 步骤描述
            - "params": 参数对象（根据动作的参数 Schema 填写）
            - "dep": 依赖的步骤 ID 列表（无依赖则为空数组）

            ## 规则
            1. 步骤 ID 必须唯一，按执行顺序编号
            2. 有数据依赖的步骤必须在 dep 中声明依赖
            3. 无依赖的步骤可并行执行
            4. 只返回 JSON 数组，不要附加任何解释

            ## 示例
            用户: "帮我查一下上周的订单总额并发送邮件给张三"
            输出:
            [{"id":"1","action":"retrieve_orders","description":"查询上周订单","params":{"period":"last_week"},"dep":[]},
             {"id":"2","action":"calculate_sum","description":"计算总额","params":{"field":"amount"},"dep":["1"]},
             {"id":"3","action":"send_email","description":"发送邮件","params":{"to":"zhangsan","subject":"订单总额"},"dep":["2"]}]
            """;

    /**
     * 规划任务 — 调用 LLM 拆解用户意图为 DAG 步骤序列.
     *
     * @param request 规划请求
     * @return 规划结果（含 executionId 和解析后的 DAG）
     */
    @Transactional
    public PlanResult plan(PlanRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        // 1. 检查是否有可用的 Handler
        if (handlerRegistry.size() == 0) {
            throw new BusinessException(400, "没有可用的动作处理器，无法进行任务规划");
        }

        // 2. 构建 LLM 提示词
        String availableActions = handlerRegistry.buildAvailableActionsBlock();
        String systemPrompt = String.format(PLANNING_SYSTEM_PROMPT, availableActions);
        String userMessage = "## 用户意图\n" + request.getUserIntent();

        // 3. 调用 LLM 进行任务规划
        log.info("[TaskPlanning] 开始规划: intent={}", request.getUserIntent());
        String llmResponse;
        try {
            llmResponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userMessage)
                    .call()
                    .content();
            log.info("[TaskPlanning] LLM 响应: {}", llmResponse);
        } catch (Exception e) {
            log.error("[TaskPlanning] LLM 调用失败", e);
            throw new BusinessException(500, "任务规划 LLM 调用失败: " + e.getMessage());
        }

        // 4. 解析 LLM 输出为 DAG 图
        DagGraph graph = dagParser.parse(llmResponse);

        // 5. 生成执行 ID 并持久化
        String executionId = IdGenerator.generate("exec");
        TaskExecution execution = TaskExecution.builder()
                .tenantId(tenantId)
                .executionId(executionId)
                .conversationId(request.getConversationId())
                .agentId(request.getAgentId())
                .planJson(toJson(graph.getNodes()))
                .status(ExecutionStatus.PENDING)
                .totalSteps(graph.size())
                .completedSteps(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        executionRepository.save(execution);

        // 6. 批量创建步骤记录
        List<TaskStepExecution> steps = new ArrayList<>();
        for (TaskNode node : graph.getNodes()) {
            ActionHandler handler = handlerRegistry.getHandler(node.getAction());
            TaskStepExecution step = TaskStepExecution.builder()
                    .executionId(executionId)
                    .stepId(node.getId())
                    .action(node.getAction())
                    .handlerClass(handler.getClass().getName())
                    .inputJson(toJson(node.getParams()))
                    .status(StepStatus.PENDING)
                    .retryCount(0)
                    .maxRetries(handler.maxRetries())
                    .createdAt(LocalDateTime.now())
                    .build();
            steps.add(step);
        }
        stepExecutionRepository.batchSave(steps);

        log.info("[TaskPlanning] 规划完成: executionId={}, totalSteps={}, levels={}",
                executionId, graph.size(), graph.getTopologicalLevels().size());

        return PlanResult.builder()
                .executionId(executionId)
                .graph(graph)
                .totalSteps(graph.size())
                .levels(graph.getTopologicalLevels().size())
                .build();
    }

    /** 查询执行计划 */
    public PlanResult getPlan(String executionId) {
        TaskExecution execution = executionRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new BusinessException(404, "执行记录不存在: " + executionId));

        List<TaskNode> nodes = parsePlanJson(execution.getPlanJson());
        DagGraph graph = dagParser.parse(execution.getPlanJson());

        return PlanResult.builder()
                .executionId(executionId)
                .graph(graph)
                .totalSteps(execution.getTotalSteps())
                .levels(graph.getTopologicalLevels() != null ? graph.getTopologicalLevels().size() : 0)
                .build();
    }

    // ==================== 工具方法 ====================

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[TaskPlanning] JSON 序列化失败", e);
            return "[]";
        }
    }

    private List<TaskNode> parsePlanJson(String planJson) {
        try {
            return objectMapper.readValue(planJson,
                    new com.fasterxml.jackson.core.type.TypeReference<List<TaskNode>>() {});
        } catch (JsonProcessingException e) {
            log.error("[TaskPlanning] Plan JSON 解析失败", e);
            return List.of();
        }
    }

    // ==================== DTOs ====================

    @Data
    public static class PlanRequest {
        private String userIntent;
        private String conversationId;
        private String agentId;
    }

    @Data
    @Builder
    public static class PlanResult {
        private String executionId;
        private DagGraph graph;
        private int totalSteps;
        private int levels;
    }
}
