package com.example.agent.application.interaction.strategy;

import com.example.agent.application.checkpoint.CheckpointService;
import com.example.agent.application.checkpoint.restore.CheckpointRestoreDispatcher;
import com.example.agent.application.checkpoint.restore.RestoreDecision;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.conversation.StreamOrchestrationService;
import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.domain.interaction.service.InteractionStrategy;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 智能对话交互策略 — 多轮对话、上下文管理、SSE 流式输出.
 * <p>
 * 委托给现有的 {@link StreamOrchestrationService} 执行完整的流式管线：
 * <ol>
 *   <li>保存用户消息</li>
 *   <li>若存在可续传检查点，先走恢复处理器（缺槽追问可在此结束本轮）</li>
 *   <li>加载会话上下文、意图识别、构建 Prompt</li>
 *   <li>LLM 流式输出（SSE token 推送）</li>
 *   <li>保存助手消息 + 长期记忆提取</li>
 * </ol>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConversationInteractionStrategy implements InteractionStrategy {

    private final StreamOrchestrationService streamOrchestrationService;
    private final MessageApplicationService messageService;
    private final CheckpointService checkpointService;
    private final CheckpointRestoreDispatcher restoreDispatcher;

    @Override
    public InteractionMode getMode() {
        return InteractionMode.CONVERSATION;
    }

    @Override
    public Object execute(InteractionContext context) {
        log.info("[ConversationStrategy] 执行对话模式: convId={}, userId={}, inputLength={}",
                context.getConversationId(), context.getUserId(),
                context.getUserInput() != null ? context.getUserInput().length() : 0);

        SseEmitter emitter = (SseEmitter) context.getEmitter();
        if (emitter == null) {
            log.error("[ConversationStrategy] SseEmitter 为空，无法执行流式对话");
            throw new IllegalStateException("对话模式需要 SseEmitter，但上下文中未提供");
        }

        messageService.saveUserMessage(context.getConversationId(), context.getUserInput());

        ConversationCheckpoint restored = checkpointService.restoredOf(context).orElse(null);
        RestoreDecision decision = restoreDispatcher.dispatch(context, restored);
        if (decision == RestoreDecision.HANDLED) {
            return null;
        }

        streamOrchestrationService.executeStreamPipeline(
                context.getConversationId(),
                context.getTenantId(),
                context.getUserId(),
                context.getUserInput(),
                emitter,
                context,
                false
        );

        return null;
    }

    @Override
    public int getPriority() {
        return 10; // 对话模式为默认兜底，优先级最高
    }
}
