package com.example.agent.domain.knowledge.service;

import java.util.List;
import java.util.Map;

/**
 * 切片策略领域接口 — Strategy 模式.
 * <p>
 * 6 种策略实现在 application 层（因为依赖 EmbeddingService 等应用层组件）,
 * 但策略接口定义在 domain 层以保证领域纯净.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface ChunkStrategyService {

    /** 策略名称 */
    String getStrategyCode();

    /** 执行切分 */
    List<ChunkResult> split(String text, Map<String, Object> config);

    /**
     * 切片结果 — 领域值对象.
     */
    record ChunkResult(
            int chunkIndex,
            String content,
            int tokenCount,
            String contentHash,
            Map<String, Object> metadata
    ) {}
}
