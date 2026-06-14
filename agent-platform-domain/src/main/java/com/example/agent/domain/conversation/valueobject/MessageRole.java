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
    USER("用户"),
    ASSISTANT("助手"),
    SYSTEM("系统"),
    TOOL("工具");

    private final String label;
}
