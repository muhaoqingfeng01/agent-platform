package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 段落 + 滑动窗口切片策略.
 * <p>
 * 适用: PDF/DOCX/TXT 通用长文本文档.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Component
public class ParagraphSlidingWindowStrategy implements ChunkStrategyService {

    @Override
    public String getStrategyCode() { return "paragraph_sliding_window"; }

    @Override
    public List<ChunkResult> split(String text, Map<String, Object> config) {
        int chunkSize = getInt(config, "chunk_size", 512);
        int chunkOverlap = getInt(config, "chunk_overlap", 50);
        int minParagraphTokens = getInt(config, "min_paragraph_tokens", 100);

        // Step 1: 按空行分割段落
        String[] paragraphs = text.split("\\n\\s*\\n");
        List<String> paraList = new ArrayList<>(Arrays.asList(paragraphs));

        // Step 2: 合并短段落
        paraList = mergeShortParagraphs(paraList, minParagraphTokens);

        // Step 3: 组装 Chunk
        List<String> segments = buildSegments(paraList, chunkSize);

        // Step 4: 滑动窗口重叠
        List<ChunkResult> results = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            StringBuilder content = new StringBuilder();
            if (i > 0 && chunkOverlap > 0) {
                String prevTail = getTail(segments.get(i - 1), chunkOverlap);
                content.append(prevTail).append("\n");
            }
            content.append(segments.get(i));

            String finalContent = content.toString();
            results.add(new ChunkResult(
                    i,
                    finalContent,
                    estimateTokens(finalContent),
                    sha256(finalContent),
                    new HashMap<>()
            ));
        }
        return results;
    }

    private List<String> mergeShortParagraphs(List<String> paragraphs, int minTokens) {
        List<String> result = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (String p : paragraphs) {
            if (p.isBlank()) continue;
            if (estimateTokens(p) < minTokens) {
                if (!buffer.isEmpty()) buffer.append("\n");
                buffer.append(p);
            } else {
                if (!buffer.isEmpty()) {
                    result.add(buffer.toString());
                    buffer.setLength(0);
                }
                result.add(p);
            }
        }
        if (!buffer.isEmpty()) result.add(buffer.toString());
        return result;
    }

    private List<String> buildSegments(List<String> paragraphs, int chunkSize) {
        List<String> segments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String p : paragraphs) {
            if (estimateTokens(current + p) <= chunkSize) {
                if (!current.isEmpty()) current.append("\n");
                current.append(p);
            } else {
                if (!current.isEmpty()) {
                    segments.add(current.toString());
                    current.setLength(0);
                }
                // 长段落 → 在句子边界处拆分
                if (estimateTokens(p) > chunkSize) {
                    segments.addAll(splitLongParagraph(p, chunkSize));
                } else {
                    current.append(p);
                }
            }
        }
        if (!current.isEmpty()) segments.add(current.toString());
        return segments;
    }

    private List<String> splitLongParagraph(String text, int chunkSize) {
        List<String> parts = new ArrayList<>();
        String[] sentences = text.split("(?<=[。！？!?\\n])");
        StringBuilder current = new StringBuilder();
        for (String s : sentences) {
            if (estimateTokens(current + s) > chunkSize && !current.isEmpty()) {
                parts.add(current.toString());
                current.setLength(0);
            }
            current.append(s);
        }
        if (!current.isEmpty()) parts.add(current.toString());
        return parts;
    }

    private String getTail(String text, int overlapTokens) {
        int len = text.length();
        int charsToTake = (int) (overlapTokens * 2.0); // 粗略估计
        int start = Math.max(0, len - charsToTake);
        return text.substring(start);
    }

    // ========== 工具方法 ==========

    static int estimateTokens(String text) {
        if (text == null || text.isEmpty()) return 0;
        int chineseChars = countChinese(text);
        int otherChars = text.length() - chineseChars;
        return (int) Math.ceil(chineseChars / 1.5 + otherChars / 4.0);
    }

    static int countChinese(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) count++;
        }
        return count;
    }

    static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(text.hashCode());
        }
    }

    static int getInt(Map<String, Object> config, String key, int defaultVal) {
        Object v = config.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException e) { return defaultVal; }
        }
        return defaultVal;
    }
}
