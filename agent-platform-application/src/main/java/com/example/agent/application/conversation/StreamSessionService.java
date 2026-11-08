package com.example.agent.application.conversation;

import com.example.agent.application.conversation.dto.StreamCancelResponse;
import com.example.agent.application.interaction.InteractionApplicationService;
import com.example.agent.common.exception.BizAssert;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.repository.MessageRepository;
import com.example.agent.domain.conversation.valueobject.MessageRole;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 流式会话应用服务 — 注册 streamId、取消、重生编排.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamSessionService {

    /** InteractionContext 属性：跳过用户消息落库（重生场景） */
    public static final String ATTR_SKIP_PERSIST_USER = "skipPersistUserMessage";
    /** InteractionContext 属性：当前 streamId */
    public static final String ATTR_STREAM_ID = "streamId";

    private final StreamRegistry streamRegistry;
    private final InteractionApplicationService interactionService;
    private final MessageRepository messageRepository;

    /**
     * 注册流、下发 stream_started，并启动交互策略流式执行.
     *
     * @return streamId
     */
    public String startStream(String modeCode, String content, String conversationId,
                              String knowledgeId, SseEmitter emitter, boolean skipPersistUser) {
        String streamId = streamRegistry.register(conversationId, emitter);
        sendStreamStarted(emitter, streamId, conversationId);
        interactionService.executeStream(modeCode, content, conversationId, knowledgeId, emitter,
                attrs -> {
                    attrs.put(ATTR_STREAM_ID, streamId);
                    if (skipPersistUser) {
                        attrs.put(ATTR_SKIP_PERSIST_USER, Boolean.TRUE);
                    }
                });
        return streamId;
    }

    public StreamCancelResponse cancel(String conversationId, String streamId) {
        BizAssert.hasText(conversationId, 400, "会话ID不能为空");
        BizAssert.hasText(streamId, 400, "流ID不能为空");
        try {
            boolean cancelled = streamRegistry.cancel(streamId, conversationId);
            return StreamCancelResponse.builder()
                    .streamId(streamId)
                    .conversationId(conversationId)
                    .cancelled(cancelled)
                    .message(cancelled ? "已停止生成" : "流已结束或不存在")
                    .build();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, e.getMessage());
        }
    }

    /**
     * 重生：定位助手消息前一条用户内容，不重复落库用户消息，重新走流式管线.
     */
    public String regenerate(String conversationId, String sourceMessageId,
                             String modeCode, String knowledgeId, SseEmitter emitter) {
        String userContent = resolveUserContentForRegenerate(conversationId, sourceMessageId);
        return startStream(modeCode, userContent, conversationId, knowledgeId, emitter, true);
    }

    /**
     * 解析重生所需的用户原文.
     */
    public String resolveUserContentForRegenerate(String conversationId, String sourceMessageId) {
        BizAssert.hasText(conversationId, 400, "会话ID不能为空");
        BizAssert.hasText(sourceMessageId, 400, "源消息ID不能为空");

        Message source = messageRepository.findByMessageId(sourceMessageId)
                .orElseThrow(() -> new BusinessException(404, "源消息不存在: " + sourceMessageId));
        BizAssert.isTrue(conversationId.equals(source.getConversationId()), 400,
                "源消息不属于当前会话");
        BizAssert.isTrue(source.getRole() == MessageRole.ASSISTANT, 400,
                "只能对助手消息执行重生");

        Message userMsg = messageRepository.findPreviousUserMessage(conversationId, sourceMessageId)
                .orElseThrow(() -> new BusinessException(400, "未找到对应的用户消息，无法重生"));
        BizAssert.hasText(userMsg.getContent(), 400, "用户消息内容为空，无法重生");
        return userMsg.getContent();
    }

    public boolean isCancelled(String streamId) {
        return streamRegistry.isCancelled(streamId);
    }

    public static boolean shouldSkipPersistUser(InteractionContext context) {
        if (context == null) {
            return false;
        }
        Object v = context.getAttribute(ATTR_SKIP_PERSIST_USER);
        return Boolean.TRUE.equals(v);
    }

    public static String streamIdOf(InteractionContext context) {
        if (context == null) {
            return null;
        }
        Object v = context.getAttribute(ATTR_STREAM_ID);
        return v != null ? v.toString() : null;
    }

    private void sendStreamStarted(SseEmitter emitter, String streamId, String conversationId) {
        try {
            emitter.send(SseEventFactory.streamStarted(streamId, conversationId));
        } catch (IOException e) {
            streamRegistry.cancel(streamId, conversationId);
            throw new BusinessException(500, "无法下发 stream_started 事件", e);
        }
    }
}
