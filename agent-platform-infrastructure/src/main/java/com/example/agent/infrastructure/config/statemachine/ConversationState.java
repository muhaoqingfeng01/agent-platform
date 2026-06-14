package com.example.agent.infrastructure.config.statemachine;

/**
 * 对话状态枚举 — State 模式的 State.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum ConversationState {
    INIT,
    AWAIT_PARAM,
    EXECUTING,
    CONFIRMATION,
    DONE,
    ERROR
}
