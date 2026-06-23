package com.example.agent.domain.conversation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 意图状态枚举.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum IntentStatus {
    ACTIVE("ACTIVE", "启用"),
    DISABLED("DISABLED", "禁用");

    private final String code;
    private final String desc;

    public static IntentStatus fromCode(String code) {
        if (code == null || code.isBlank()) return ACTIVE;
        for (IntentStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
