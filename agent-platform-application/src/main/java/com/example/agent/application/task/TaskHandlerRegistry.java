package com.example.agent.application.task;

import com.example.agent.domain.task.service.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TaskHandler 注册中心 — 自动发现所有 TaskHandler 实现.
 * <p>
 * 复用 ChunkStrategyFactory 的 List 注入 + afterPropertiesSet 模式.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
public class TaskHandlerRegistry implements InitializingBean {

    private final List<TaskHandler<?>> handlers;
    private final Map<String, TaskHandler<?>> handlerMap = new ConcurrentHashMap<>();

    public TaskHandlerRegistry(List<TaskHandler<?>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void afterPropertiesSet() {
        if (CollectionUtils.isEmpty(handlers)) {
            log.warn("[TaskHandlerRegistry] 未发现任何 TaskHandler 实现");
        }
        handlers.forEach(h -> {
            handlerMap.put(h.getTaskType(), h);
            log.info("[TaskHandlerRegistry] 注册 TaskHandler: type={}, class={}",
                    h.getTaskType(), h.getClass().getSimpleName());
        });
    }

    /**
     * 根据任务类型获取 Handler.
     *
     * @throws IllegalArgumentException 如果未找到对应的 Handler
     */
    @SuppressWarnings("unchecked")
    public <T> TaskHandler<T> getHandler(String taskType) {
        TaskHandler<?> handler = handlerMap.get(taskType);
        if (handler == null) {
            throw new IllegalArgumentException("未找到 TaskHandler: " + taskType);
        }
        return (TaskHandler<T>) handler;
    }
}
