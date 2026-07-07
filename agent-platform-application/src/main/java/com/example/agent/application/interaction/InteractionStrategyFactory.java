package com.example.agent.application.interaction;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.interaction.service.InteractionStrategy;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 交互模式策略工厂 — 自动发现与调度.
 * <p>
 * 通过 Spring {@link InitializingBean} 自动收集所有 {@link InteractionStrategy} 实现，
 * 按 {@link InteractionMode} 建立索引，根据请求模式路由到对应策略。
 *
 * <h3>自动发现机制</h3>
 * Spring 自动注入所有标注 {@code @Component} 的 {@link InteractionStrategy} 实现类，
 * 在 {@link #afterPropertiesSet()} 中按 mode 建立映射表。
 * 新增策略只需添加 {@code @Component} 即可，无需修改本类。
 *
 * <h3>设计参考</h3>
 * 对齐项目现有工厂模式：{@code ChunkStrategyFactory}、{@code ActionHandlerRegistry}、
 * {@code MemoryExtractorRegistry}、{@code RerankerRegistry}
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InteractionStrategyFactory implements InitializingBean {

    private final ConcurrentMap<InteractionMode, InteractionStrategy> strategyMap = Maps.newConcurrentMap();
    private final List<InteractionStrategy> strategies;

    @Override
    public void afterPropertiesSet() {
        if (CollectionUtils.isEmpty(strategies)) {
            log.warn("[InteractionFactory] 未发现任何 InteractionStrategy 实现");
            return;
        }

        // 按优先级降序排列，同 mode 取最高优先级
        strategies.stream()
                .sorted(Comparator.comparingInt(InteractionStrategy::getPriority).reversed())
                .forEach(strategy -> {
                    InteractionMode mode = strategy.getMode();
                    InteractionStrategy existing = strategyMap.get(mode);
                    if (existing != null) {
                        log.warn("[InteractionFactory] 模式 {} 已有策略 {} (优先级={}), 跳过 {} (优先级={})",
                                mode.getDesc(),
                                existing.getClass().getSimpleName(), existing.getPriority(),
                                strategy.getClass().getSimpleName(), strategy.getPriority());
                        return;
                    }
                    strategyMap.put(mode, strategy);
                    log.info("[InteractionFactory] 注册策略: mode={}, class={}, priority={}",
                            mode.getDesc(), strategy.getClass().getSimpleName(), strategy.getPriority());
                });

        log.info("[InteractionFactory] 初始化完成，已注册 {} 个策略: {}", strategyMap.size(), strategyMap.keySet());
    }

    /**
     * 根据交互模式获取对应策略.
     *
     * @param mode 交互模式
     * @return 对应的策略实现
     * @throws BusinessException 如果模式不被支持（未注册对应策略）
     */
    public InteractionStrategy getStrategy(InteractionMode mode) {
        if (mode == null) {
            throw new BusinessException(400, "交互模式不能为空");
        }
        InteractionStrategy strategy = strategyMap.get(mode);
        if (strategy == null) {
            throw new BusinessException(400, "不支持的交互模式: " + mode.getDesc()
                    + " (" + mode.getCode() + ")，已注册模式: " + strategyMap.keySet());
        }
        return strategy;
    }

    /**
     * 根据模式编码获取对应策略.
     *
     * @param modeCode 模式编码
     * @return 对应的策略实现
     */
    public InteractionStrategy getStrategy(String modeCode) {
        InteractionMode mode = InteractionMode.fromCode(modeCode);
        return getStrategy(mode);
    }

    /**
     * 获取所有已注册的模式列表.
     *
     * @return 已注册的模式编码集合
     */
    public List<InteractionMode> getRegisteredModes() {
        return List.copyOf(strategyMap.keySet());
    }
}
