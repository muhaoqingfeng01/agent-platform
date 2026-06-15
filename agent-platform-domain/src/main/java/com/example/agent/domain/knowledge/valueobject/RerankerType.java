package com.example.agent.domain.knowledge.valueobject;

/**
 * Reranker 类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public enum RerankerType {
    NONE,
    CROSS_ENCODER,
    COLBERT,
    LLM;

    public static RerankerType fromCode(String code) {
        if (code == null || code.isBlank()) return NONE;
        for (RerankerType t : values()) {
            if (t.name().equalsIgnoreCase(code)) return t;
        }
        throw new IllegalArgumentException("未知 Reranker 类型: " + code);
    }
}
