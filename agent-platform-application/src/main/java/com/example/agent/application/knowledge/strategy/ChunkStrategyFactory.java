package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import com.example.agent.domain.knowledge.valueobject.ChunkStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 切片策略工厂 — 根据策略枚举选择对应实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Component
public class ChunkStrategyFactory {

    private final Map<String, ChunkStrategyService> strategyMap;

    /** Spring 自动注入所有 ChunkStrategyService 实现 */
    public ChunkStrategyFactory(List<ChunkStrategyService> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(ChunkStrategyService::getStrategyCode, s -> s));
        log.info("[ChunkStrategyFactory] 初始化完成，加载策略数量: {}", this.strategyMap.size());
    }

    /**
     * 根据策略编码获取实现.
     */
    public ChunkStrategyService getStrategy(ChunkStrategy strategy) {
        return getStrategy(strategy.getCode());
    }

    /**
     * 根据策略字符串获取实现.
     */
    public ChunkStrategyService getStrategy(String strategyCode) {
        ChunkStrategyService service = strategyMap.get(strategyCode);
        if (service == null) {
            throw new IllegalArgumentException("未找到切片策略: " + strategyCode);
        }
        return service;
    }

    /**
     * 获取所有可用策略.
     */
    public List<String> getAvailableStrategies() {
        return List.copyOf(strategyMap.keySet());
    }
}
