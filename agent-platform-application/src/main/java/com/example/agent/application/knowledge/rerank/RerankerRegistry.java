package com.example.agent.application.knowledge.rerank;

import com.example.agent.domain.knowledge.service.Reranker;
import com.example.agent.domain.knowledge.valueobject.RerankerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reranker 注册中心 — Strategy Registry 模式.
 *
 * <p>自动发现所有 Reranker 实现并建立类型映射.
 * 若某类型有多个实现，取第一个.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
public class RerankerRegistry {

    private final Map<RerankerType, Reranker> registry = new EnumMap<>(RerankerType.class);

    public RerankerRegistry(List<Reranker> rerankers) {
        for (Reranker reranker : rerankers) {
            RerankerType type = reranker.supportedType();
            if (!registry.containsKey(type)) {
                registry.put(type, reranker);
                log.info("[Reranker] 注册: type={}, impl={}", type, reranker.getClass().getSimpleName());
            }
        }
        log.info("[Reranker] 注册中心初始化完成，已注册 {} 个 Reranker", registry.size());
    }

    /**
     * 根据类型获取 Reranker 实例.
     */
    public Optional<Reranker> get(RerankerType type) {
        return Optional.ofNullable(registry.get(type));
    }

    /**
     * 判断指定类型是否有已注册的实现.
     */
    public boolean hasImpl(RerankerType type) {
        return registry.containsKey(type);
    }
}
