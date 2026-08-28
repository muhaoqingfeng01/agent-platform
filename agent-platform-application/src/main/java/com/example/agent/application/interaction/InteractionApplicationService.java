package com.example.agent.application.interaction;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.example.agent.application.interaction.dto.InteractionResponse;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.interaction.service.InteractionStrategy;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.infrastructure.config.nacos.AiModelConfig;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 交互应用服务 — 编排多模式交互流程.
 * <p>
 * Controller 通过本服务完成模式路由与策略调度，自身仅负责 HTTP 适配。
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>同步交互：构建上下文 → 获取策略 → 执行 → 返回统一响应</li>
 *   <li>流式交互：构建上下文 → 获取策略 → 异步提交线程池 → SSE 推送</li>
 *   <li>模式查询：返回所有已注册的模式编码</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionApplicationService {

    private final InteractionStrategyFactory strategyFactory;
    private final ThreadPoolExecutor streamExecutor;
    private final AiModelConfig aiModelConfig;
    /**
     * 同步执行交互（非流式模式：知识检索等）.
     *
     * @param modeCode      模式编码
     * @param content       用户输入内容
     * @param conversationId 会话 ID（可选）
     * @param knowledgeId   知识库 ID（可选）
     * @param searchConfig  检索配置（可选）
     * @return 统一交互响应
     */
    public InteractionResponse executeSync(String modeCode, String content,
                                            String conversationId, String knowledgeId,
                                            Map<String, Object> searchConfig) {
        InteractionMode mode = InteractionMode.fromCode(modeCode);

        // 流式模式应使用 executeStream 端点
        if (mode == InteractionMode.CONVERSATION) {
            throw new BusinessException(400, "对话模式请使用流式端点");
        }
        if (mode == InteractionMode.TASK_EXECUTION) {
            throw new BusinessException(400, "任务执行模式请使用流式端点");
        }
        if (mode == InteractionMode.ANALYSIS) {
            throw new BusinessException(400, "分析推理模式请使用流式端点");
        }

        InteractionContext context = buildContext(mode, content, conversationId,
                knowledgeId, searchConfig, null);
        InteractionStrategy strategy = strategyFactory.getStrategy(mode);

        Object result = strategy.execute(context);
        InteractionResponse response = (result instanceof InteractionResponse)
                ? (InteractionResponse) result
                : InteractionResponse.success(mode.getCode(), result);

        log.info("[Interaction] 同步执行完成: mode={}, success={}", mode.getDesc(), response.isSuccess());
        return response;
    }

    /**
     * 流式执行交互（SSE 流式模式：智能对话、知识检索 RAG 等）.
     * <p>
     * 在独立线程中执行策略，通过 SseEmitter 推送结果。
     * 支持所有实现了 {@code executeStream} 的交互模式。
     * <p>
     * <b>默认模式：</b>若 {@code modeCode} 为空或无效，默认回退到 {@code CONVERSATION}，
     * 确保旧版客户端不传 mode 字段时行为不变。
     *
     * @param modeCode      模式编码（null/blank 默认 CONVERSATION）
     * @param content       用户输入内容
     * @param conversationId 会话 ID
     * @param knowledgeId   知识库 ID（KNOWLEDGE_SEARCH 模式可选）
     * @param emitter       SSE 发射器
     */
    public void executeStream(String modeCode, String content, String conversationId,
                               String knowledgeId, SseEmitter emitter) {
        InteractionMode mode = resolveMode(modeCode);

        // 分析推理需在 HTTP 线程校验权限（Sa-Token ThreadLocal 不进线程池）
        Map<String, Object> analysisFlags = null;
        if (mode == InteractionMode.ANALYSIS) {
            analysisFlags = resolveAnalysisPermissions();
        }

        InteractionContext context = buildContext(mode, content, conversationId,
                knowledgeId, analysisFlags, emitter);
        InteractionStrategy strategy = strategyFactory.getStrategy(mode);

        log.info("aiModelConfig:{}", JSON.toJSONString(aiModelConfig.getConfig()));

        streamExecutor.submit(() -> {
            try {
                strategy.executeStream(context);
            } catch (Exception e) {
                log.error("[Interaction] 流式执行异常: mode={}, convId={}", mode.getDesc(), conversationId, e);
                emitter.completeWithError(e);
            }
        });
    }

    /**
     * 查询所有已注册的交互模式编码.
     *
     * @return 模式编码列表
     */
    public List<String> getRegisteredModeCodes() {
        return strategyFactory.getRegisteredModes().stream()
                .map(InteractionMode::getCode)
                .toList();
    }

    // ==================== 内部方法 ====================

    /**
     * 解析交互模式 — null/blank/invalid 时安全回退到 CONVERSATION.
     * <p>
     * 将模式解析逻辑从 Controller 层下沉到应用层，确保所有调用方行为一致。
     */
    private InteractionMode resolveMode(String modeCode) {
        if (modeCode == null || modeCode.isBlank()) {
            return InteractionMode.CONVERSATION;
        }
        try {
            return InteractionMode.fromCode(modeCode);
        } catch (IllegalArgumentException e) {
            log.warn("[Interaction] 不支持的交互模式: {}，回退到 CONVERSATION", modeCode);
            return InteractionMode.CONVERSATION;
        }
    }

    /**
     * 构建交互上下文 — 根据模式和参数选择合适的工厂方法.
     */
    private InteractionContext buildContext(InteractionMode mode, String content,
                                             String conversationId, String knowledgeId,
                                             Map<String, Object> searchConfig, Object emitter) {
        Long tenantId = TenantContext.getCurrentTenantId();

        return switch (mode) {
            case CONVERSATION -> InteractionContext.forConversation(
                    content, conversationId, tenantId,
                    TenantContext.getCurrentUserId(), emitter);
            case KNOWLEDGE_SEARCH -> {
                // 流式模式（有 emitter）→ 使用流式工厂方法（含 conversationId + userId）
                if (emitter != null) {
                    yield InteractionContext.forKnowledgeSearchStream(
                            content, conversationId, knowledgeId, tenantId,
                            TenantContext.getCurrentUserId(), emitter);
                }
                // 同步模式 → 使用同步工厂方法
                yield InteractionContext.forKnowledgeSearch(
                        content, knowledgeId, tenantId, searchConfig);
            }
            case TASK_EXECUTION -> InteractionContext.forTaskExecution(
                    content, conversationId, tenantId,
                    TenantContext.getCurrentUserId(), emitter);
            case ANALYSIS -> InteractionContext.forAnalysis(
                    content, conversationId, tenantId,
                    TenantContext.getCurrentUserId(), emitter, searchConfig);
        };
    }

    /**
     * 校验分析推理权限 — 需具备 observability:read 或 evaluation:read 之一，否则 403.
     * <p>
     * 在提交异步线程前调用，避免 Sa-Token 上下文丢失。
     */
    private Map<String, Object> resolveAnalysisPermissions() {
        boolean canObs = StpUtil.hasPermission("observability:read");
        boolean canEval = StpUtil.hasPermission("evaluation:read");
        if (!canObs && !canEval) {
            throw new NotPermissionException("observability:read");
        }
        Map<String, Object> flags = new HashMap<>();
        flags.put("canObservability", canObs);
        flags.put("canEvaluation", canEval);
        return flags;
    }
}
