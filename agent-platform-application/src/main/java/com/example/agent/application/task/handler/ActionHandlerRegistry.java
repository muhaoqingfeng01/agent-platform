package com.example.agent.application.task.handler;

import com.example.agent.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ActionHandler 注册中心 — Mediator + Observer 模式.
 *
 * <p>Spring 启动时自动扫描所有 {@link ActionHandler} 实现类并注册到内存索引。
 * <p>同时作为 LLM 规划提示词的可用动作数据源。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class ActionHandlerRegistry implements InitializingBean {

    private final Map<String, ActionHandler> handlers = new ConcurrentHashMap<>();
    private final List<ActionHandler> handlerList;

    /**
     * Spring 自动注入所有 ActionHandler 实现.
     */
    public ActionHandlerRegistry(List<ActionHandler> handlers) {
        this.handlerList = handlers;
    }

    /**
     * 容器启动后自动注册所有 Handler — Observer 模式.
     * 检测重复的 actionType 并抛出异常阻止启动。
     */
    @Override
    public void afterPropertiesSet() {
        if (handlerList.isEmpty()) {
            log.warn("[ActionHandlerRegistry] 未发现任何 ActionHandler 实现，任务执行功能将不可用");
            return;
        }
        for (ActionHandler handler : handlerList) {
            String type = handler.getActionType();
            if (handlers.containsKey(type)) {
                throw new IllegalStateException(
                        "重复的 ActionHandler actionType: " + type
                        + " [" + handlers.get(type).getClass().getSimpleName()
                        + " vs " + handler.getClass().getSimpleName() + "]");
            }
            handlers.put(type, handler);
            log.info("[ActionHandlerRegistry] 已注册: {} → {} (highRisk={}, maxRetries={}, timeoutMs={})",
                    type, handler.getClass().getSimpleName(),
                    handler.isHighRisk(), handler.maxRetries(), handler.timeoutMs());
        }
    }

    /**
     * 根据 actionType 获取对应的 Handler.
     *
     * @param actionType 动作类型
     * @return 对应的 Handler
     * @throws BusinessException 如果未找到对应的 Handler
     */
    public ActionHandler getHandler(String actionType) {
        ActionHandler handler = handlers.get(actionType);
        if (handler == null) {
            throw new BusinessException(400, "未知的动作类型: " + actionType
                    + "，可用类型: " + String.join(", ", handlers.keySet()));
        }
        return handler;
    }

    /** 获取所有已注册的 actionType */
    public Set<String> getAvailableActions() {
        return Set.copyOf(handlers.keySet());
    }

    /** 获取所有已注册的 Handler */
    public List<ActionHandler> getAllHandlers() {
        return List.copyOf(handlers.values());
    }

    /**
     * 构建 LLM 任务规划提示词中的「可用动作」描述块.
     */
    public String buildAvailableActionsBlock() {
        return handlers.values().stream()
                .map(h -> String.format("- **%s**: %s (参数: %s) %s",
                        h.getActionType(),
                        h.getDescription(),
                        h.getParamsSchema(),
                        h.isHighRisk() ? "⚠️高风险" : ""))
                .collect(Collectors.joining("\n"));
    }

    /** 获取已注册的 Handler 数量 */
    public int size() {
        return handlers.size();
    }
}
