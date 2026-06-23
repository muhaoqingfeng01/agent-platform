package com.example.agent.domain.knowledge.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Milvus 一致性级别枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@AllArgsConstructor
public enum ConsistencyLevel {
    /** 强一致 — 写入立即可检索 */
    STRONG("STRONG", "强一致"),
    /** 有界一致 — 允许短暂不一致（推荐默认） */
    BOUNDED("BOUNDED", "有界一致"),
    /** 最终一致 — 高吞吐，允许延迟可见 */
    EVENTUALLY("EVENTUALLY", "最终一致");

    private final String code;
    private final String desc;

    public static ConsistencyLevel fromCode(String code) {
        if (code == null || code.isBlank()) return BOUNDED;
        for (ConsistencyLevel e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知一致性级别: " + code);
    }
}
