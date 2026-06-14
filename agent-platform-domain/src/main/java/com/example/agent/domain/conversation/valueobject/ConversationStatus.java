package com.example.agent.domain.conversation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

/**
 * 会话状态值对象 — State 模式.
 * <p>
 * 每个状态枚举自带合法的状态转移集合，禁止非法转移.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ConversationStatus {

    /** 活跃 — 可收发消息 */
    ACTIVE("活跃") {
        @Override
        public Set<ConversationStatus> allowedTransitions() {
            return EnumSet.of(CLOSED, ARCHIVED);
        }
    },

    /** 已关闭 — 不可收发，可重开或归档 */
    CLOSED("已关闭") {
        @Override
        public Set<ConversationStatus> allowedTransitions() {
            return EnumSet.of(ACTIVE, ARCHIVED);
        }
    },

    /** 已归档 — 只读，不可逆 */
    ARCHIVED("已归档") {
        @Override
        public Set<ConversationStatus> allowedTransitions() {
            return EnumSet.noneOf(ConversationStatus.class);
        }
    };

    private final String label;

    /** 当前状态允许转移到的目标状态集合 */
    public abstract Set<ConversationStatus> allowedTransitions();

    /** 校验状态转移是否合法 */
    public boolean canTransitionTo(ConversationStatus target) {
        return allowedTransitions().contains(target);
    }
}
