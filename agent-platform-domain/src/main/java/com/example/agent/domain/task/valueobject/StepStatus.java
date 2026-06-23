package com.example.agent.domain.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 步骤执行状态枚举 — State 模式.
 *
 * <p>生命周期: PENDING → RUNNING → SUCCESS | FAILED | SKIPPED | TIMEOUT
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum StepStatus {

    PENDING("PENDING", "等待执行"),
    RUNNING("RUNNING", "执行中"),
    SUCCESS("SUCCESS", "执行成功"),
    FAILED("FAILED", "执行失败"),
    SKIPPED("SKIPPED", "已跳过"),
    TIMEOUT("TIMEOUT", "执行超时");

    private final String code;
    private final String desc;

    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || this == SKIPPED || this == TIMEOUT;
    }

    public boolean isFailure() {
        return this == FAILED || this == TIMEOUT;
    }

    public static StepStatus fromCode(String code) {
        if (code == null || code.isBlank()) return PENDING;
        for (StepStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知步骤状态: " + code);
    }
}
