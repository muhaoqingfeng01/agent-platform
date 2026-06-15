package com.example.agent.application.knowledge.strategy;

import com.example.agent.domain.knowledge.service.ChunkStrategyService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Markdown 标题层级切片策略.
 * <p>
 * 适用: .md / Markdown / Tika 转换后的 HTML.
 * <p>
 * 以标题（# ~ ######）作为切分边界，保留章节路径作为元数据.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Component
public class MarkdownHeaderAwareStrategy implements ChunkStrategyService {

    @Override
    public String getStrategyCode() { return "markdown_header_aware"; }

    @Override
    public List<ChunkResult> split(String text, Map<String, Object> config) {
        int minChunkTokens = ParagraphSlidingWindowStrategy.getInt(config, "min_chunk_tokens", 256);
        int maxChunkTokens = ParagraphSlidingWindowStrategy.getInt(config, "max_chunk_tokens", 1024);
        boolean includeHeaderPath = getBool(config, "include_header_path_in_content", true);
        boolean codeBlockAtomic = getBool(config, "code_block_atomic", true);

        // Step 1: 按标题分割
        String[] lines = text.split("\\n");
        List<Section> sections = parseSections(lines, codeBlockAtomic);

        // Step 2: 按层级组装 chunk
        List<ChunkResult> results = new ArrayList<>();
        int index = 0;
        List<String> headerStack = new ArrayList<>();
        StringBuilder currentContent = new StringBuilder();

        for (Section section : sections) {
            // 更新标题栈
            if (section.headingLevel > 0) {
                while (!headerStack.isEmpty() && getLevel(headerStack.get(headerStack.size() - 1)) >= section.headingLevel) {
                    headerStack.remove(headerStack.size() - 1);
                }
                headerStack.add(section.heading);
            }

            String headerPath = String.join(" > ", headerStack);
            String sectionText = includeHeaderPath && !headerPath.isEmpty()
                    ? headerPath + "\n" + section.content
                    : section.content;

            int sectionTokens = ParagraphSlidingWindowStrategy.estimateTokens(sectionText);

            if (!currentContent.isEmpty() &&
                    ParagraphSlidingWindowStrategy.estimateTokens(currentContent.toString()) + sectionTokens > maxChunkTokens) {
                String chunkText = currentContent.toString();
                results.add(new ChunkResult(
                        index++, chunkText,
                        ParagraphSlidingWindowStrategy.estimateTokens(chunkText),
                        ParagraphSlidingWindowStrategy.sha256(chunkText),
                        Map.of("header_path", headerPath)
                ));
                currentContent.setLength(0);
            }

            if (!currentContent.isEmpty()) currentContent.append("\n");
            currentContent.append(sectionText);
        }

        // 最后一段
        if (!currentContent.isEmpty()) {
            String chunkText = currentContent.toString();
            results.add(new ChunkResult(
                    index, chunkText,
                    ParagraphSlidingWindowStrategy.estimateTokens(chunkText),
                    ParagraphSlidingWindowStrategy.sha256(chunkText),
                    Map.of("header_path", String.join(" > ", headerStack))
            ));
        }

        return results;
    }

    private List<Section> parseSections(String[] lines, boolean codeBlockAtomic) {
        List<Section> sections = new ArrayList<>();
        StringBuilder currentContent = new StringBuilder();
        String currentHeading = "";
        int currentLevel = 0;
        boolean inCodeBlock = false;

        for (String line : lines) {
            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                currentContent.append(line).append("\n");
                continue;
            }

            // 非代码块内的标题检测
            if (!inCodeBlock && line.matches("^#{1,6}\\s+.+")) {
                // 保存上一个 section
                if (!currentContent.isEmpty()) {
                    sections.add(new Section(currentHeading, currentLevel, currentContent.toString().trim()));
                    currentContent.setLength(0);
                }
                currentLevel = line.indexOf(' ');
                currentHeading = line.substring(currentLevel + 1).trim();
                currentLevel = (int) line.chars().limit(6).filter(c -> c == '#').count();
                continue;
            }

            currentContent.append(line).append("\n");
        }

        if (!currentContent.isEmpty()) {
            sections.add(new Section(currentHeading, currentLevel, currentContent.toString().trim()));
        }

        return sections;
    }

    private int getLevel(String heading) {
        return (int) heading.chars().limit(6).filter(c -> c == '#').count();
    }

    private boolean getBool(Map<String, Object> config, String key, boolean defaultVal) {
        Object v = config.get(key);
        if (v instanceof Boolean b) return b;
        if (v instanceof String s) return "true".equalsIgnoreCase(s);
        return defaultVal;
    }

    private record Section(String heading, int headingLevel, String content) {}
}
