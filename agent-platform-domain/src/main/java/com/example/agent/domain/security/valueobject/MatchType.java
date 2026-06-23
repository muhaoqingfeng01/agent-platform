package com.example.agent.domain.security.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感词匹配方式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MatchType {
    /** 精确匹配 */
    EXACT("EXACT", "精确匹配"),
    /** 正则表达式匹配 */
    REGEX("REGEX", "正则表达式"),
    /** 语义相似度匹配 */
    SEMANTIC("SEMANTIC", "语义相似度");

    private final String code;
    private final String desc;

    public static MatchType fromCode(String code) {
        if (code == null || code.isBlank()) return EXACT;
        for (MatchType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
