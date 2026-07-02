package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import com.example.agent.domain.knowledge.valueobject.ChunkStrategy;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 切片策略工厂 — 根据策略枚举选择对应实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChunkStrategyFactory implements InitializingBean {

    private static final Map<ChunkStrategy, ChunkStrategyService> strategyMap = Maps.newConcurrentMap();

    private final List<ChunkStrategyService> strategies;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(strategies)) {
            log.warn("[ChunkStrategyFactory] 未发现任何 ChunkStrategyService 实现");
        }
        strategies.forEach(service ->
                {
                    ChunkStrategy chunkStrategy = service.getStrategyCode();
                    strategyMap.put(chunkStrategy, service);
                }
        );
    }

    /**
     * 根据策略编码获取实现.
     */
    public ChunkStrategyService getStrategy(ChunkStrategy strategy) {
        return strategyMap.get(strategy);
    }



}
