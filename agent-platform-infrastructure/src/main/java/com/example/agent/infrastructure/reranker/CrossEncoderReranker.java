package com.example.agent.infrastructure.reranker;

import com.example.agent.domain.knowledge.service.Reranker;
import com.example.agent.domain.knowledge.valueobject.RerankerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Cross-Encoder Reranker — 本地部署 bge-reranker-v2-m3.
 *
 * <p>原理：将 (query, document) 对一起输入 Cross-Encoder，
 * 模型输出相关性分数（比 Bi-Encoder 的余弦相似度更精准）。
 *
 * <p>当前为 STUB 实现 — 使用 RRF 分数降序排序，等待模型服务部署后替换.
 * 启用条件: knowledge.reranker.cross-encoder.enabled=true
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component("crossEncoderReranker")
@ConditionalOnProperty(name = "knowledge.reranker.cross-encoder.enabled", havingValue = "true", matchIfMissing = false)
public class CrossEncoderReranker implements Reranker {

    @Override
    public RerankerType supportedType() {
        return RerankerType.CROSS_ENCODER;
    }

    /**
     * STUB: 按 RRF 分数降序排序（与原始 RRF 结果一致）.
     * <p>TODO: 替换为真正的 Cross-Encoder 模型推理调用.
     */
    @Override
    public List<RerankerHit> rerank(String query, List<RerankerHit> candidates, int topK) {
        if (candidates.size() <= topK) {
            return candidates;
        }

        log.debug("[CrossEncoder-STUB] 使用 RRF 分数排序（等待模型部署）: candidates={}, topK={}",
            candidates.size(), topK);

        return candidates.stream()
            .sorted(Comparator.comparingDouble(RerankerHit::rrfScore).reversed())
            .limit(topK)
            .toList();
    }
}
