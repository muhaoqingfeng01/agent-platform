package com.example.agent.domain.knowledge.valueobject;

/**
 * Milvus 一致性级别枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public enum ConsistencyLevel {
    /** 强一致 — 写入立即可检索 */
    STRONG,
    /** 有界一致 — 允许短暂不一致（推荐默认） */
    BOUNDED,
    /** 最终一致 — 高吞吐，允许延迟可见 */
    EVENTUALLY;

    public static ConsistencyLevel fromCode(String code) {
        if (code == null || code.isBlank()) return BOUNDED;
        for (ConsistencyLevel l : values()) {
            if (l.name().equalsIgnoreCase(code)) return l;
        }
        throw new IllegalArgumentException("未知一致性级别: " + code);
    }
}
