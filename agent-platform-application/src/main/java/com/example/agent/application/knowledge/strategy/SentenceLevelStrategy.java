package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 句子级切片策略.
 * <p>
 * 适用: FAQ/对话记录/客服话术/短问答对.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Component
public class SentenceLevelStrategy implements ChunkStrategyService {

    @Override
    public String getStrategyCode() { return "sentence_level"; }

    @Override
    public List<ChunkResult> split(String text, Map<String, Object> config) {
        int chunkSize = ParagraphSlidingWindowStrategy.getInt(config, "chunk_size", 512);
        int overlapSentences = ParagraphSlidingWindowStrategy.getInt(config, "overlap_sentences", 2);
        boolean detectQaPairs = getBool(config, "detect_qa_pairs", true);
        int maxSentencesPerChunk = ParagraphSlidingWindowStrategy.getInt(config, "max_sentences_per_chunk", 15);

        // Step 1: 句子分割
        List<String> sentences = splitSentences(text);

        // Step 2: QA 对检测 + 绑定
        if (detectQaPairs) {
            sentences = bindQaPairs(sentences);
        }

        // Step 3: 句子组装成 chunk
        List<ChunkResult> results = new ArrayList<>();
        int index = 0;
        int i = 0;
        while (i < sentences.size()) {
            StringBuilder chunk = new StringBuilder();
            int sentenceCount = 0;
            int tokenCount = 0;
            List<Integer> sourceSentences = new ArrayList<>();

            while (i < sentences.size()
                    && tokenCount + ParagraphSlidingWindowStrategy.estimateTokens(sentences.get(i)) <= chunkSize
                    && sentenceCount < maxSentencesPerChunk) {
                if (!chunk.isEmpty()) chunk.append(" ");
                chunk.append(sentences.get(i));
                tokenCount += ParagraphSlidingWindowStrategy.estimateTokens(sentences.get(i));
                sourceSentences.add(i);
                sentenceCount++;
                i++;
            }

            if (!chunk.isEmpty()) {
                results.add(new ChunkResult(
                        index++, chunk.toString(), tokenCount,
                        ParagraphSlidingWindowStrategy.sha256(chunk.toString()),
                        Map.of("sentence_count", sentenceCount, "source_sentences", sourceSentences)
                ));
            }

            // 回退 overlap 个句子
            if (i < sentences.size() && overlapSentences > 0) {
                i = Math.max(0, i - overlapSentences);
            }
            if (sentenceCount == 0) i++; // 防止死循环
        }

        return results;
    }

    private List<String> splitSentences(String text) {
        // 中文+英文混合分句
        List<String> sentences = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            current.append(c);
            if (c == '。' || c == '！' || c == '？' || c == ';' || c == '；'
                    || c == '\n' || c == '.' || c == '!' || c == '?') {
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

    private List<String> bindQaPairs(List<String> sentences) {
        List<String> result = new ArrayList<>();
        boolean prevIsQuestion = false;
        StringBuilder qaPair = new StringBuilder();

        for (String s : sentences) {
            boolean isQuestion = s.trim().endsWith("?") || s.trim().endsWith("？")
                    || s.trim().startsWith("Q:") || s.trim().startsWith("问:");

            if (prevIsQuestion && !isQuestion) {
                // 前一句是问句，当前句是答句 → 绑定
                qaPair.append(" ").append(s);
                result.add(qaPair.toString().trim());
                qaPair.setLength(0);
                prevIsQuestion = false;
            } else if (prevIsQuestion && isQuestion) {
                // 连续问句 → 前面的单独处理
                result.add(qaPair.toString().trim());
                qaPair.setLength(0);
                qaPair.append(s);
                prevIsQuestion = true;
            } else if (isQuestion) {
                if (!qaPair.isEmpty()) {
                    result.add(qaPair.toString().trim());
                    qaPair.setLength(0);
                }
                qaPair.append(s);
                prevIsQuestion = true;
            } else {
                if (!qaPair.isEmpty()) {
                    result.add(qaPair.toString().trim());
                    qaPair.setLength(0);
                }
                result.add(s);
                prevIsQuestion = false;
            }
        }
        if (!qaPair.isEmpty()) result.add(qaPair.toString().trim());
        return result;
    }

    private boolean getBool(Map<String, Object> config, String key, boolean defaultVal) {
        Object v = config.get(key);
        if (v instanceof Boolean b) return b;
        if (v instanceof String s) return "true".equalsIgnoreCase(s);
        return defaultVal;
    }
}
