package com.example.agent.domain.interaction.service;

import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;

/**
 * 交互模式策略接口 — 策略模式核心抽象.
 * <p>
 * 每种交互模式（智能对话、知识库检索、任务执行、分析推理等）实现此接口，
 * 通过 {@code @Component} 标注后由 {@code InteractionStrategyFactory} 自动收集注册。
 *
 * <h3>新模式接入步骤</h3>
 * <ol>
 *   <li>在 {@link InteractionMode} 枚举中新增模式常量</li>
 *   <li>实现本接口，标注 {@code @Component}</li>
 *   <li>实现 {@link #getMode()} 返回对应枚举值</li>
 *   <li>实现 {@link #execute(InteractionContext)} 编写核心逻辑</li>
 * </ol>
 *
 * <h3>设计约束</h3>
 * <ul>
 *   <li>每种模式一个实现类 — 单一职责原则</li>
 *   <li>策略内部自行处理异常并转换为友好响应 — 故障隔离</li>
 *   <li>流式输出通过 {@link InteractionContext#getEmitter()} 获取 SseEmitter</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
public interface InteractionStrategy {

    /**
     * 返回本策略处理的交互模式.
     *
     * @return 交互模式枚举值
     */
    InteractionMode getMode();

    /**
     * 执行交互逻辑.
     * <p>
     * 根据模式不同，返回值类型也不同：
     * <ul>
 *   <li>流式模式（CONVERSATION / TASK_EXECUTION）：返回 null，通过 context.emitter 推送 SSE 事件</li>
 *   <li>同步模式（KNOWLEDGE_SEARCH）：返回对应的结果 DTO</li>
     * </ul>
     *
     * @param context 交互上下文
     * @return 执行结果（流式模式返回 null）
     */
    Object execute(InteractionContext context);

    /**
     * 执行流式交互逻辑（SSE 推送模式）.
     * <p>
     * 对于需要流式输出的模式（如 KNOWLEDGE_SEARCH 的 RAG 生成），
     * 实现此方法以支持 SSE 流式推送。
     * 默认实现委托给 {@link #execute(InteractionContext)} 进行同步处理。
     *
     * @param context 交互上下文（必须包含 emitter）
     * @since 1.7.0
     */
    default void executeStream(InteractionContext context) {
        // 默认降级为同步执行，子类可按需覆写
        Object result = execute(context);
        if (result != null && context.getEmitter() != null) {
            // 同步结果一次性写入 SSE
            try {
                org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter =
                        (org.springframework.web.servlet.mvc.method.annotation.SseEmitter) context.getEmitter();
                emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                        .name("result")
                        .data(result));
                emitter.complete();
            } catch (Exception e) {
                // 连接已关闭，忽略
            }
        }
    }

    /**
     * 判断是否支持指定的交互模式.
     *
     * @param mode 交互模式
     * @return true 表示本策略处理该模式
     */
    default boolean supports(InteractionMode mode) {
        return getMode() == mode;
    }

    /**
     * 策略优先级（多个策略匹配同一模式时取最高优先级）.
     * <p>默认 0，数值越大优先级越高.
     *
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }
}
