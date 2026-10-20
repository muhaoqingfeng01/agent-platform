package com.example.agent.application.checkpoint.restore;

import com.example.agent.application.checkpoint.CheckpointService;
import com.example.agent.application.checkpoint.SlotAskSupport;
import com.example.agent.domain.conversation.valueobject.AgentPhase;
import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AWAIT_PARAM 断点续传 — 将用户本轮输入填入下一缺失槽位.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AwaitParamCheckpointRestoreHandler implements CheckpointRestoreHandler {

    private final CheckpointService checkpointService;
    private final SlotAskSupport slotAskSupport;

    @Override
    public boolean supports(InteractionContext context, ConversationCheckpoint checkpoint) {
        return checkpoint != null
                && checkpoint.getState() == AgentPhase.AWAIT_PARAM
                && (context.getMode() == null || context.getMode() == InteractionMode.CONVERSATION);
    }

    @Override
    public RestoreDecision handle(InteractionContext context, ConversationCheckpoint checkpoint) {
        SlotCollection current = checkpointService.slotsOf(context);
        SlotCollection filled = current.fillNext(context.getUserInput());
        checkpointService.putSlots(context, filled);

        if (!filled.isComplete()) {
            SseEmitter emitter = emitterOf(context);
            if (emitter == null) {
                log.warn("[Checkpoint] AWAIT_PARAM 续传缺少 Emitter，回退默认管线");
                return RestoreDecision.CONTINUE;
            }
            slotAskSupport.askAndComplete(
                    emitter, context.getConversationId(), checkpoint.getIntentCode(), filled);
            return RestoreDecision.HANDLED;
        }

        checkpointService.recordTransition(
                context.getConversationId(), AgentPhase.EXECUTING,
                checkpoint.getIntentCode(), filled, checkpoint.getExecutionId(), checkpoint.getStepIndex());
        log.info("[Checkpoint] 槽位已齐，继续执行: convId={}, intent={}",
                context.getConversationId(), checkpoint.getIntentCode());
        return RestoreDecision.CONTINUE;
    }

    private SseEmitter emitterOf(InteractionContext context) {
        if (context.getEmitter() instanceof SseEmitter emitter) {
            return emitter;
        }
        return null;
    }
}
