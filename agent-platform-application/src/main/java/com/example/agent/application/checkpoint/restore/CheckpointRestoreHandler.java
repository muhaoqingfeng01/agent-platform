package com.example.agent.application.checkpoint.restore;

import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.domain.interaction.valueobject.InteractionContext;

/**
 * 检查点恢复处理器 — 按相位/模式扩展，新增场景不必改 Dispatcher.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
public interface CheckpointRestoreHandler {

    boolean supports(InteractionContext context, ConversationCheckpoint checkpoint);

    /**
     * 处理恢复逻辑.
     *
     * @return {@link RestoreDecision#HANDLED} 表示本轮 SSE 已结束；
     *         {@link RestoreDecision#CONTINUE} 表示已注入上下文，继续默认管线
     */
    RestoreDecision handle(InteractionContext context, ConversationCheckpoint checkpoint);
}
