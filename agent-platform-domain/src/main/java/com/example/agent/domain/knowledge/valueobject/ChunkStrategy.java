package com.example.agent.domain.knowledge.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档切片策略枚举 — 6 种策略支持.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@AllArgsConstructor
public enum ChunkStrategy {

    /** 段落 + 滑动窗口切片 — 通用长文档（PDF/DOCX） */
    PARAGRAPH_SLIDING_WINDOW("paragraph_sliding_window", "段落+滑动窗口"),

    /** 固定长度切片 — 规范文档/API/CSV */
    FIXED_SIZE("fixed_size", "固定长度"),

    /** Markdown 标题层级切片 — .md / HTML */
    MARKDOWN_HEADER_AWARE("markdown_header_aware", "Markdown标题感知"),

    /** 句子级切片 — FAQ/对话 */
    SENTENCE_LEVEL("sentence_level", "句子级"),

    /** 递归字符分割 — 无结构纯文本兜底 */
    RECURSIVE_CHAR_SPLIT("recursive_char_split", "递归字符分割"),

    /** 语义切片 — 高质量长文 */
    SEMANTIC("semantic", "语义切片");

    private final String code;
    private final String desc;

    public static ChunkStrategy fromCode(String code) {
        if (code == null || code.isBlank()) return null;
        for (ChunkStrategy e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知切片策略: " + code);
    }
}
