package com.example.agent.domain.knowledge.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Reranker 类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@AllArgsConstructor
public enum RerankerType {
    NONE("NONE", "无"),
    CROSS_ENCODER("CROSS_ENCODER", "交叉编码器"),
    COLBERT("COLBERT", "ColBERT"),
    LLM("LLM", "LLM重排序");

    private final String code;
    private final String desc;

    public static RerankerType fromCode(String code) {
        if (code == null || code.isBlank()) return NONE;
        for (RerankerType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return NONE;
    }
}
