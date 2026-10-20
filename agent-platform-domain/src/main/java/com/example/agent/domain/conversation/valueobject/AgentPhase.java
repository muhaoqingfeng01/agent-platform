package com.example.agent.domain.conversation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

/**
 * Agent 执行相位 — 一轮对话/任务的进展状态，与会话生命周期 {@link ConversationStatus} 正交.
 * <p>
 * 检查点只持久化本枚举，不绑定 Spring StateMachine，便于 B7 领域迁移与其它模式复用。
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Getter
@AllArgsConstructor
public enum AgentPhase {

    /** 初始 — 尚无未完成槽位或任务 */
    INIT("INIT", "初始") {
        @Override
        public Set<AgentPhase> allowedTransitions() {
            return EnumSet.of(AWAIT_PARAM, EXECUTING, ERROR);
        }
    },

    /** 等待参数 — 意图已识别但槽位未齐 */
    AWAIT_PARAM("AWAIT_PARAM", "等待参数补充") {
        @Override
        public Set<AgentPhase> allowedTransitions() {
            return EnumSet.of(AWAIT_PARAM, EXECUTING, ERROR);
        }
    },

    /** 执行中 — 调用 LLM / 工具 / DAG */
    EXECUTING("EXECUTING", "执行中") {
        @Override
        public Set<AgentPhase> allowedTransitions() {
            return EnumSet.of(CONFIRMATION, DONE, ERROR);
        }
    },

    /** 等待确认 — 高风险操作需人工审批 */
    CONFIRMATION("CONFIRMATION", "等待确认") {
        @Override
        public Set<AgentPhase> allowedTransitions() {
            return EnumSet.of(EXECUTING, DONE, ERROR);
        }
    },

    /** 本轮完成 */
    DONE("DONE", "完成") {
        @Override
        public Set<AgentPhase> allowedTransitions() {
            return EnumSet.of(INIT, AWAIT_PARAM, EXECUTING);
        }
    },

    /** 本轮异常 */
    ERROR("ERROR", "异常") {
        @Override
        public Set<AgentPhase> allowedTransitions() {
            return EnumSet.of(INIT, AWAIT_PARAM, EXECUTING);
        }
    };

    private final String code;
    private final String desc;

    public abstract Set<AgentPhase> allowedTransitions();

    public boolean canTransitTo(AgentPhase target) {
        return target != null && allowedTransitions().contains(target);
    }

    /** 断线后新 stream 需要注入上下文的相位 */
    public boolean isResumable() {
        return this == AWAIT_PARAM || this == EXECUTING || this == CONFIRMATION;
    }

    /** 终态：恢复时视为新一轮 */
    public boolean isTerminal() {
        return this == DONE || this == ERROR;
    }

    public static AgentPhase fromCode(String code) {
        if (code == null || code.isBlank()) {
            return INIT;
        }
        for (AgentPhase e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("未知 AgentPhase: " + code);
    }
}
