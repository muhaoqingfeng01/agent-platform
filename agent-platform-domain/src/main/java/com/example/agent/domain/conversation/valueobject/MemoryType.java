package com.example.agent.domain.conversation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 长期记忆类型枚举 — 按加载优先级排序.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MemoryType {

    /** 事实 — 永不过期 */
    FACT("事实", 3),
    /** 偏好 — 90 天过期，最高加载优先级 */
    PREFERENCE("偏好", 1),
    /** 上下文 — 7 天过期 */
    CONTEXT("上下文", 2),
    /** 摘要 — 30 天过期 */
    SUMMARY("摘要", 4);

    private final String label;
    /** 加载优先级（数字越小越先注入 Prompt） */
    private final int loadPriority;
}
