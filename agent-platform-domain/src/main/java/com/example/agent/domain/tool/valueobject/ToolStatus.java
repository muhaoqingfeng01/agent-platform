package com.example.agent.domain.tool.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工具状态枚举 — 控制工具是否可被 Agent 调用.
 *
 * <p>只有 ACTIVE 状态的工具才会出现在工具清单中并被 LLM 可见.
 * DISABLED 的工具保留配置但不可调用，相当于软开关.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ToolStatus {

    /** 启用状态 — 工具可以被 Agent 发现和调用 */
    ACTIVE("启用"),

    /** 禁用状态 — 工具不可被调用，保留配置数据 */
    DISABLED("禁用");

    /** 中文标签，用于前端展示 */
    private final String label;

    /**
     * 获取中文显示名称.
     *
     * @return 中文标签
     */
    public String toChinese() {
        return this.label;
    }

    /**
     * 根据代码字符串查找对应的枚举值.
     *
     * @param code 状态代码（大小写不敏感）
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果代码不匹配
     */
    public static ToolStatus fromCode(String code) {
        for (ToolStatus status : values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的工具状态: " + code + "，有效值: ACTIVE, DISABLED");
    }
}
