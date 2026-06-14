package com.example.agent.infrastructure.config.statemachine;

/**
 * 对话事件枚举 — State 模式的 Event.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum ConversationEvent {
    MESSAGE_RECEIVED,
    PARAMS_COMPLETE,
    COMPLETED,
    CONFIRMATION_NEEDED,
    USER_CONFIRMED,
    USER_REJECTED,
    ERROR_OCCURRED
}
