package com.example.agent.domain.evaluation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评测执行状态枚举.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum EvaluationRunStatusEnums {

    PENDING("PENDING", "等待执行"),
    RUNNING("RUNNING", "执行中"),
    COMPLETED("COMPLETED", "已完成"),
    FAILED("FAILED", "失败");

    private final String code;
    private final String desc;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }

    public static EvaluationRunStatusEnums fromCode(String code) {
        if (code == null || code.isBlank()) return PENDING;
        for (EvaluationRunStatusEnums e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知评测状态: " + code);
    }
}
