package com.example.agent.domain.knowledge.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 向量距离度量类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@AllArgsConstructor
public enum MetricType {
    COSINE("COSINE", "余弦距离"),
    IP("IP", "内积"),
    L2("L2", "欧氏距离");

    private final String code;
    private final String desc;

    public static MetricType fromCode(String code) {
        if (code == null || code.isBlank()) return COSINE;
        for (MetricType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知度量类型: " + code);
    }
}
