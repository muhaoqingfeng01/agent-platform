package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import com.example.agent.domain.knowledge.service.EmbeddingServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 语义切片策略 — 基于 embedding 相似度检测语义断点.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Component
public class SemanticStrategy implements ChunkStrategyService {

    private final EmbeddingServiceClient embeddingClient;

    public SemanticStrategy(EmbeddingServiceClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }

    @Override
    public String getStrategyCode() { return "semantic"; }

    @Override
    public List<ChunkResult> split(String text, Map<String, Object> config) {
        int breakpointThresholdInt = ParagraphSlidingWindowStrategy.getInt(config, "breakpoint_threshold_percent", 45);
        double breakpointThreshold = breakpointThresholdInt / 100.0;

        List<String> sentences = splitSentences(text);
        if (sentences.size() < 3) {
            return List.of(new ChunkResult(0, text,
                    ParagraphSlidingWindowStrategy.estimateTokens(text),
                    ParagraphSlidingWindowStrategy.sha256(text),
                    Map.of("breakpoint_score", 0.0, "adjacent_chunk_similarity", 0.0)));
        }

        if (sentences.size() > 500) {
            log.warn("[SemanticStrategy] 文档句子数({})超过500，降级为 paragraph_sliding_window", sentences.size());
            return fallbackToParagraph(text, config);
        }

        List<float[]> embeddings;
        try {
            embeddings = batchEmbed(sentences);
        } catch (Exception e) {
            log.error("[SemanticStrategy] Embedding 调用失败，降级为 paragraph_sliding_window", e);
            return fallbackToParagraph(text, config);
        }

        List<Integer> breakpoints = new ArrayList<>();
        for (int i = 0; i < embeddings.size() - 1; i++) {
            double similarity = cosineSimilarity(embeddings.get(i), embeddings.get(i + 1));
            if (similarity < breakpointThreshold) {
                breakpoints.add(i);
            }
        }

        List<ChunkResult> results = new ArrayList<>();
        int start = 0;
        int index = 0;
        for (int bp : breakpoints) {
            String chunkText = String.join(" ", sentences.subList(start, bp + 1));
            results.add(new ChunkResult(index++, chunkText,
                    ParagraphSlidingWindowStrategy.estimateTokens(chunkText),
                    ParagraphSlidingWindowStrategy.sha256(chunkText),
                    Map.of("breakpoint_score", 0.0, "adjacent_chunk_similarity", 0.0)));
            start = bp + 1;
        }
        if (start < sentences.size()) {
            String chunkText = String.join(" ", sentences.subList(start, sentences.size()));
            results.add(new ChunkResult(index, chunkText,
                    ParagraphSlidingWindowStrategy.estimateTokens(chunkText),
                    ParagraphSlidingWindowStrategy.sha256(chunkText),
                    Map.of("breakpoint_score", 0.0, "adjacent_chunk_similarity", 0.0)));
        }

        return results;
    }

    private List<String> splitSentences(String text) {
        List<String> sentences = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            current.append(c);
            if (c == '。' || c == '！' || c == '？' || c == '\n' || c == '.' || c == '!' || c == '?') {
                String s = current.toString().trim();
                if (!s.isEmpty()) sentences.add(s);
                current.setLength(0);
            }
        }
        if (!current.isEmpty()) {
            String s = current.toString().trim();
            if (!s.isEmpty()) sentences.add(s);
        }
        return sentences;
    }

    private List<float[]> batchEmbed(List<String> sentences) {
        List<float[]> embeddings = new ArrayList<>();
        for (String s : sentences) {
            embeddings.add(embeddingClient.embed(s));
        }
        return embeddings;
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private List<ChunkResult> fallbackToParagraph(String text, Map<String, Object> config) {
        ParagraphSlidingWindowStrategy fallback = new ParagraphSlidingWindowStrategy();
        return fallback.split(text, config);
    }
}
