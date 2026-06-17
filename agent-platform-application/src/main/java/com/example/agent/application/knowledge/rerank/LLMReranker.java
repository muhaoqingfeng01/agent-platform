package com.example.agent.application.knowledge.rerank;

import com.example.agent.domain.knowledge.service.Reranker;
import com.example.agent.domain.knowledge.valueobject.RerankerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * LLM-as-Reranker — 使用 DeepSeek 对候选片段打分.
 *
 * <p>优点：无需额外模型部署，即插即用
 * <p>缺点：每次检索增加 1 次 LLM 调用（可用小模型降低成本）
 *
 * <p>Prompt 设计：要求 LLM 返回 0-10 的相关性分数，仅返回数字.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component("llmReranker")
@RequiredArgsConstructor
public class LLMReranker implements Reranker {

    private final ChatClient.Builder chatClientBuilder;

    private static final String RERANK_PROMPT = """
        You are a relevance scorer. Rate how relevant the document chunk is to the query.

        Query: %s
        Document: %s

        Score (0-10, integer only):""";

    @Override
    public RerankerType supportedType() {
        return RerankerType.LLM;
    }

    @Override
    public List<RerankerHit> rerank(String query, List<RerankerHit> candidates, int topK) {
        if (candidates.size() <= topK) {
            return candidates;
        }

        ChatClient chatClient = chatClientBuilder.build();
        List<RerankerHit> scored = new ArrayList<>();

        for (RerankerHit candidate : candidates) {
            try {
                String prompt = String.format(RERANK_PROMPT, query, candidate.content());
                String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

                // 解析分数
                double score = parseScore(response);
                scored.add(new RerankerHit(
                    candidate.chunkId(), candidate.documentId(), candidate.content(),
                    candidate.chunkIndex(), candidate.vectorScore(), candidate.rrfScore(), score));

                log.debug("[LLM-Reranker] chunk={}, score={}", candidate.chunkId(), score);
            } catch (Exception e) {
                log.warn("[LLM-Reranker] 打分失败: chunk={}, error={}", candidate.chunkId(), e.getMessage());
                // 失败的候选项保留但得分为 0
                scored.add(new RerankerHit(
                    candidate.chunkId(), candidate.documentId(), candidate.content(),
                    candidate.chunkIndex(), candidate.vectorScore(), candidate.rrfScore(), 0.0));
            }
        }

        // 按 rerankScore 降序 → 取 topK
        return scored.stream()
            .sorted(Comparator.comparingDouble(RerankerHit::rerankScore).reversed())
            .limit(topK)
            .toList();
    }

    /**
     * 从 LLM 响应中解析数值分数.
     */
    private double parseScore(String response) {
        if (response == null || response.isBlank()) return 0.0;
        try {
            // 提取第一个数字
            String cleaned = response.trim().replaceAll("[^0-9.]", " ");
            String[] parts = cleaned.split("\\s+");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    double score = Double.parseDouble(part);
                    return Math.max(0, Math.min(10, score));  // 钳制到 0-10
                }
            }
        } catch (NumberFormatException e) {
            log.warn("[LLM-Reranker] 无法解析分数: {}", response);
        }
        return 0.0;
    }
}
