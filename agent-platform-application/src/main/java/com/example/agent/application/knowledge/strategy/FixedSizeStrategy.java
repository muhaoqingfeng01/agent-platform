package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 固定长度切片策略.
 * <p>
 * 适用: 规范化文档/API参考/CSV/代码文件.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Component
public class FixedSizeStrategy implements ChunkStrategyService {

    @Override
    public String getStrategyCode() { return "fixed_size"; }

    @Override
    public List<ChunkResult> split(String text, Map<String, Object> config) {
        int chunkSize = ParagraphSlidingWindowStrategy.getInt(config, "chunk_size", 512);
        int chunkOverlap = ParagraphSlidingWindowStrategy.getInt(config, "chunk_overlap", 64);
        boolean enableBoundarySoftening = getBool(config, "enable_boundary_softening", true);
        int maxBoundaryShift = ParagraphSlidingWindowStrategy.getInt(config, "max_boundary_shift", 50);

        int step = chunkSize - chunkOverlap;
        if (step <= 0) step = chunkSize;

        List<ChunkResult> results = new ArrayList<>();
        int pos = 0;
        int index = 0;
        int textLen = text.length();

        while (pos < textLen) {
            int end = Math.min(pos + chunkSize * 2, textLen); // 粗略估计: 1 token ≈ 2 char

            // 软边界修正
            if (enableBoundarySoftening && end < textLen) {
                int adjustedEnd = findNearestBoundary(text, end, maxBoundaryShift);
                if (adjustedEnd > 0) end = adjustedEnd;
            }

            String chunkText = text.substring(pos, end);
            results.add(new ChunkResult(
                    index++,
                    chunkText,
                    ParagraphSlidingWindowStrategy.estimateTokens(chunkText),
                    ParagraphSlidingWindowStrategy.sha256(chunkText),
                    Map.of("start_char", pos, "end_char", end, "segment_index", index)
            ));

            pos = end - Math.min(chunkOverlap * 2, end - pos);
            if (pos >= textLen) break;
        }

        return results;
    }

    private int findNearestBoundary(String text, int from, int maxShift) {
        int searchStart = Math.max(0, from - maxShift);
        String window = text.substring(searchStart, Math.min(text.length(), from + maxShift));
        // 查找最近的句子边界
        char[] boundaries = {'。', '！', '？', '!', '?', '\n'};
        int bestPos = -1;
        for (int i = window.length() - 1; i >= 0; i--) {
            char c = window.charAt(i);
            for (char b : boundaries) {
                if (c == b) {
                    bestPos = searchStart + i + 1;
                    if (Math.abs(bestPos - from) <= maxShift) return bestPos;
                }
            }
        }
        return -1;
    }

    private boolean getBool(Map<String, Object> config, String key, boolean defaultVal) {
        Object v = config.get(key);
        if (v instanceof Boolean b) return b;
        if (v instanceof String s) return "true".equalsIgnoreCase(s);
        return defaultVal;
    }
}
