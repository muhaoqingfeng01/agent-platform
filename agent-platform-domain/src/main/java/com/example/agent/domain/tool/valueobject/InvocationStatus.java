package com.example.agent.domain.tool.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工具调用状态枚举 — 记录每次工具调用的执行结果.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum InvocationStatus {

    /** 调用成功 — 工具正常返回结果 */
    SUCCESS("SUCCESS", "调用成功"),

    /** 调用失败 — 工具执行过程中抛出异常 */
    FAILED("FAILED", "调用失败"),

    /** 调用超时 — 工具执行超过预设时间限制 */
    TIMEOUT("TIMEOUT", "调用超时"),

    /** 审批驳回 — 高风险工具被审批人拒绝 */
    REJECTED("REJECTED", "审批驳回");

    private final String code;
    private final String desc;

    /**
     * 根据代码字符串查找对应的枚举值.
     *
     * @param code 状态代码（大小写不敏感）
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果代码不匹配
     */
    public static InvocationStatus fromCode(String code) {
        if (code == null || code.isBlank()) return SUCCESS;
        for (InvocationStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知的调用状态: " + code + "，有效值: SUCCESS, FAILED, TIMEOUT, REJECTED");
    }
}
