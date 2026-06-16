package com.example.agent.domain.security.valueobject;

/**
 * 敏感词匹配方式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum MatchType {
    /** 精确匹配 */
    EXACT,
    /** 正则表达式匹配 */
    REGEX,
    /** 语义相似度匹配 */
    SEMANTIC;

    public static MatchType fromCode(String code) {
        if (code == null) return EXACT;
        try {
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EXACT;
        }
    }
}
