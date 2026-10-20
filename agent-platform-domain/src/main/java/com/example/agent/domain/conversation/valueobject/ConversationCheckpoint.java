package com.example.agent.domain.conversation.valueobject;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 会话检查点 — Memento 快照，不是消息表替代.
 * <p>
 * 只存相位、意图、已填槽位、执行进度；完整 LLM 上下文仍由 SessionMemory 管理。
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Getter
@Builder
public class ConversationCheckpoint {

    private String checkpointId;
    private String conversationId;
    private AgentPhase state;
    private String intentCode;
    /** 槽位 JSON：{@code {"required":[...],"filled":{...}}} */
    private String slotsJson;
    private String executionId;
    private Integer stepIndex;
    private LocalDateTime createdAt;

    public boolean isResumable() {
        return state != null && state.isResumable();
    }

    public boolean isAwaitParam() {
        return state == AgentPhase.AWAIT_PARAM;
    }

    public String stateCode() {
        return state != null ? state.getCode() : AgentPhase.INIT.getCode();
    }
}
