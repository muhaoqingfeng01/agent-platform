package com.example.agent.domain.knowledge.valueobject;

/**
 * 向量距离度量类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public enum MetricType {
    COSINE,
    IP,
    L2;

    public static MetricType fromCode(String code) {
        if (code == null || code.isBlank()) return COSINE;
        for (MetricType t : values()) {
            if (t.name().equalsIgnoreCase(code)) return t;
        }
        throw new IllegalArgumentException("未知度量类型: " + code);
    }
}
