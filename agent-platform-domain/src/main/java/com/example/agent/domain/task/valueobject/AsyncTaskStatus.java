package com.example.agent.domain.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异步任务状态枚举 — 通用任务中心使用.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Getter
@AllArgsConstructor
public enum AsyncTaskStatus {

    /** 已提交，等待线程池执行 */
    SUBMITTED("SUBMITTED", "已提交"),

    /** 执行中 */
    RUNNING("RUNNING", "执行中"),

    /** 执行成功 */
    COMPLETED("COMPLETED", "已完成"),

    /** 执行失败 */
    FAILED("FAILED", "失败"),

    /** 超时 */
    TIMEOUT("TIMEOUT", "超时");

    private final String code;
    private final String desc;

    public static AsyncTaskStatus fromCode(String code) {
        if (code == null || code.isBlank()) return null;
        for (AsyncTaskStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知任务状态: " + code);
    }

    /** 是否终态 */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == TIMEOUT;
    }

    /** 是否活跃状态 */
    public boolean isActive() {
        return this == SUBMITTED || this == RUNNING;
    }
}
