package com.example.agent.domain.conversation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息角色枚举.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MessageRole {
    USER("USER", "用户"),
    ASSISTANT("ASSISTANT", "助手"),
    SYSTEM("SYSTEM", "系统"),
    TOOL("TOOL", "工具");

    private final String code;
    private final String desc;

    public static MessageRole fromCode(String code) {
        if (code == null || code.isBlank()) return USER;
        for (MessageRole e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
