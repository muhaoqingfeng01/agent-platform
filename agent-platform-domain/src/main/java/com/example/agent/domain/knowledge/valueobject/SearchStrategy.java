package com.example.agent.domain.knowledge.valueobject;

/**
 * 检索策略预设枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public enum SearchStrategy {
    /** 高精度 — 医疗/法律/金融 */
    PRECISE("precise", "高精度"),
    /** 平衡 — 通用问答（默认） */
    BALANCED("balanced", "平衡"),
    /** 低延迟 — 实时对话 */
    FAST("fast", "低延迟"),
    /** 高召回 — 探索性搜索 */
    RECALL("recall", "高召回"),
    /** 极速 — 流式首 token 优化 */
    TURBO("turbo", "极速");

    private final String code;
    private final String description;

    SearchStrategy(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static SearchStrategy fromCode(String code) {
        if (code == null || code.isBlank()) return BALANCED;
        for (SearchStrategy s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("未知检索策略: " + code);
    }
}
