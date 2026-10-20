package com.example.agent.application.checkpoint;

import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.AgentPhase;
import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 缺槽追问 — 统一 SSE 文案与检查点写入，供首次缺槽与断点续传复用.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlotAskSupport {

    private final CheckpointService checkpointService;
    private final MessageApplicationService messageService;

    /**
     * 保存 AWAIT_PARAM 检查点，SSE 提问「请继续补充 xx」后结束本轮.
     *
     * @return true 表示已结束 stream
     */
    public boolean askAndComplete(SseEmitter emitter, String conversationId,
                                  String intentCode, SlotCollection slots) {
        checkpointService.recordTransition(
                conversationId, AgentPhase.AWAIT_PARAM, intentCode, slots, null, null);
        String prompt = slots != null ? slots.continuePrompt() : "请继续补充必要信息";
        try {
            emitter.send(SseEventFactory.thinking("检测到未完成的槽位，请继续补充。"));
            emitter.send(SseEventFactory.token(prompt));
        } catch (Exception e) {
            log.warn("[Checkpoint] 发送槽位追问失败: convId={}", conversationId, e);
        }
        Message assistantMsg = messageService.saveAssistantMessage(conversationId, prompt, 0);
        try {
            emitter.send(SseEventFactory.done(0, assistantMsg.getMessageId()));
            emitter.complete();
        } catch (Exception e) {
            log.warn("[Checkpoint] 结束槽位追问 SSE 失败: convId={}", conversationId, e);
        }
        log.info("[Checkpoint] 等待槽位: convId={}, intent={}, prompt={}", conversationId, intentCode, prompt);
        return true;
    }
}
