package com.example.agent.domain.conversation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 意图分类枚举.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum IntentCategory {
    FAQ("FAQ", "常见问答"),
    TASK("TASK", "任务执行"),
    CHITCHAT("CHITCHAT", "闲聊"),
    MULTI_STEP("MULTI_STEP", "多步骤任务");

    private final String code;
    private final String desc;

    public static IntentCategory fromCode(String code) {
        if (code == null || code.isBlank()) return FAQ;
        for (IntentCategory e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
