package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 递归字符分割策略 — LangChain 默认策略增强版.
 * <p>
 * 适用: 无结构纯文本/日志/混合格式 — 最通用兜底方案.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Component
public class RecursiveCharSplitStrategy implements ChunkStrategyService {

    private static final List<String> DEFAULT_SEPARATORS = List.of(
            "\n\n", "\n", "。", ". ", "！", "？", "! ", "? ",
            "；", ";", "，", ",", " ", ""
    );

    @Override
    public String getStrategyCode() { return "recursive_char_split"; }

    @Override
    public List<ChunkResult> split(String text, Map<String, Object> config) {
        int chunkSize = ParagraphSlidingWindowStrategy.getInt(config, "chunk_size", 800);
        int chunkOverlap = ParagraphSlidingWindowStrategy.getInt(config, "chunk_overlap", 100);

        List<String> separators = getSeparators(config);
        List<String> chunks = splitRecursive(text, separators, 0, chunkSize);

        // 添加重叠
        List<ChunkResult> results = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String content = chunks.get(i);
            if (i > 0 && chunkOverlap > 0) {
                String prevTail = getTail(chunks.get(i - 1), chunkOverlap);
                content = prevTail + "\n" + content;
            }
            results.add(new ChunkResult(
                    i, content,
                    ParagraphSlidingWindowStrategy.estimateTokens(content),
                    ParagraphSlidingWindowStrategy.sha256(content),
                    Map.of("split_depth", 0)
            ));
        }
        return results;
    }

    private List<String> splitRecursive(String text, List<String> separators, int depth, int chunkSize) {
        if (depth >= separators.size()) {
            // 终极降级：逐字符切割
            return List.of(text);
        }

        String separator = separators.get(depth);
        List<String> result = new ArrayList<>();

        // 按分隔符分割
        String[] parts;
        if (separator.isEmpty()) {
            // 逐字符
            parts = text.split("");
        } else {
            parts = text.split(Pattern.quote(separator));
        }

        StringBuilder current = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;

            if (ParagraphSlidingWindowStrategy.estimateTokens(current + separator + part) <= chunkSize) {
                if (!current.isEmpty()) current.append(separator);
                current.append(part);
            } else {
                if (!current.isEmpty()) {
                    if (ParagraphSlidingWindowStrategy.estimateTokens(current.toString()) > chunkSize) {
                        // 递归降级
                        result.addAll(splitRecursive(current.toString(), separators, depth + 1, chunkSize));
                    } else {
                        result.add(current.toString());
                    }
                    current.setLength(0);
                }
                // 单个 part 仍然超大 → 递归降级
                if (ParagraphSlidingWindowStrategy.estimateTokens(part) > chunkSize) {
                    result.addAll(splitRecursive(part, separators, depth + 1, chunkSize));
                } else {
                    current.append(part);
                }
            }
        }

        if (!current.isEmpty()) {
            if (ParagraphSlidingWindowStrategy.estimateTokens(current.toString()) > chunkSize) {
                result.addAll(splitRecursive(current.toString(), separators, depth + 1, chunkSize));
            } else {
                result.add(current.toString());
            }
        }

        return result;
    }

    private String getTail(String text, int overlapTokens) {
        int charsToTake = (int) (overlapTokens * 2.5);
        int start = Math.max(0, text.length() - charsToTake);
        return text.substring(start);
    }

    @SuppressWarnings("unchecked")
    private List<String> getSeparators(Map<String, Object> config) {
        Object v = config.get("separator_priority_list");
        if (v instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return DEFAULT_SEPARATORS;
    }
}
