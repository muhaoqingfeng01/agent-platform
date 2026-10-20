package com.example.agent.application.checkpoint.restore;

import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 检查点恢复分发 — 按 supports 命中第一个处理器；无处理器则 CONTINUE（仅注入上下文）.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckpointRestoreDispatcher {

    private final List<CheckpointRestoreHandler> handlers;

    public RestoreDecision dispatch(InteractionContext context, ConversationCheckpoint checkpoint) {
        if (context == null || checkpoint == null || !checkpoint.isResumable()) {
            return RestoreDecision.CONTINUE;
        }
        for (CheckpointRestoreHandler handler : handlers) {
            if (handler.supports(context, checkpoint)) {
                RestoreDecision decision = handler.handle(context, checkpoint);
                log.debug("[Checkpoint] 恢复处理器: {}, decision={}",
                        handler.getClass().getSimpleName(), decision);
                return decision;
            }
        }
        return RestoreDecision.CONTINUE;
    }
}
