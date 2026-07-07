package com.example.agent.domain.interaction.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交互模式枚举 — 定义系统支持的多模式交互类型.
 * <p>
 * 每种模式对应一个 {@link com.example.agent.domain.interaction.service.InteractionStrategy} 实现，
 * 由 {@code InteractionStrategyFactory} 自动发现并注册。
 *
 * <pre>
 * 使用规范（对齐项目枚举统一规范）：
 *   - 序列化用 {@code getCode()}，反序列化用 {@code fromCode(code)}
 *   - 同类型枚举比较用 {@code ==}
 *   - 禁止使用 {@code name()} 或 {@code valueOf()}
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Getter
@AllArgsConstructor
public enum InteractionMode {

    /** 智能对话 — 多轮对话、上下文管理、SSE 流式输出 */
    CONVERSATION("CONVERSATION", "智能对话"),

    /** 知识库检索 — 向量检索 + 关键词检索 + RRF 融合 + Reranker 精排 */
    KNOWLEDGE_SEARCH("KNOWLEDGE_SEARCH", "知识库检索");

    private final String code;
    private final String desc;

    /**
     * 根据 code 获取枚举实例（大小写不敏感）.
     *
     * @param code 模式编码
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果 code 不匹配任何模式
     */
    public static InteractionMode fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("交互模式 code 不能为空");
        }
        for (InteractionMode e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("不支持的交互模式: " + code + "，可用值: CONVERSATION, KNOWLEDGE_SEARCH");
    }
}
