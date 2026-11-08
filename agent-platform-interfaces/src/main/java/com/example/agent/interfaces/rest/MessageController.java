package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.conversation.MessageListResponse;
import com.example.agent.application.conversation.MessageApplicationService.MessageResponse;
import com.example.agent.application.conversation.StreamSessionService;
import com.example.agent.application.conversation.dto.StreamCancelResponse;
import com.example.agent.application.optimization.event.MessageFeedbackEvent;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.domain.conversation.valueobject.FeedbackType;
import com.example.agent.infrastructure.annotation.RateLimit;
import com.example.agent.infrastructure.config.sse.SseEmitterFactory;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.interfaces.dto.request.message.MessageCancelRequest;
import com.example.agent.interfaces.dto.request.message.MessageSendRequest;
import com.example.agent.interfaces.dto.request.message.MessageListRequest;
import com.example.agent.interfaces.dto.request.message.MessageLoadBeforeRequest;
import com.example.agent.interfaces.dto.request.message.MessageFeedbackRequest;
import com.example.agent.interfaces.dto.request.message.MessageRegenerateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 消息收发 Controller — 纯粹 HTTP 适配层.
 * <p>
 * 流式端点经 {@link StreamSessionService} 注册 streamId 后路由到交互策略，
 * 支持 CONVERSATION / KNOWLEDGE_SEARCH / TASK_EXECUTION / ANALYSIS / APPROVAL，
 * 并提供 cancel / regenerate。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "对话管理", description = "消息收发与流式响应")
public class MessageController {

    private final MessageApplicationService messageService;
    private final StreamSessionService streamSessionService;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/api/v1/conversations/messages/send")
    @RateLimit(resource = "chat:send", qps = 50)
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（非流式）")
    public Result<MessageResponse> sendMessage(@Valid @RequestBody MessageSendRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.sendMessage", request, (req) ->
                MessageResponse.from(
                        messageService.saveUserMessage(req.getConversationId(), req.getContent())));
    }

    @PostMapping(value = "/api/v1/conversations/messages/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @RateLimit(resource = "chat:stream", qps = 30)
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（SSE 流式）— 支持 CONVERSATION / KNOWLEDGE_SEARCH / TASK_EXECUTION / ANALYSIS / APPROVAL")
    public SseEmitter streamChat(@Valid @RequestBody MessageSendRequest request,
                                 HttpServletResponse response) {
        SseEmitter emitter = SseEmitterFactory.create(300_000L, response);
        // 注册 streamId 后统一走策略工厂路由
        streamSessionService.startStream(
                request.getMode(),
                request.getContent(),
                request.getConversationId(),
                request.getKnowledgeId(),
                emitter,
                false);
        return emitter;
    }

    @PostMapping("/api/v1/conversations/messages/cancel")
    @RateLimit(resource = "chat:send", qps = 50)
    @SaCheckPermission("conversation:send")
    @Operation(summary = "取消进行中的 SSE 流式生成")
    public Result<StreamCancelResponse> cancelStream(@Valid @RequestBody MessageCancelRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.cancelStream", request, (req) ->
                streamSessionService.cancel(req.getConversationId(), req.getStreamId()));
    }

    @PostMapping(value = "/api/v1/conversations/messages/regenerate",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @RateLimit(resource = "chat:stream", qps = 30)
    @SaCheckPermission("conversation:send")
    @Operation(summary = "基于助手消息重生（SSE）— 保留旧消息并追加新助手回复")
    public SseEmitter regenerate(@Valid @RequestBody MessageRegenerateRequest request,
                                 HttpServletResponse response) {
        // 先校验源消息，避免 emitter 已创建却抛出业务异常
        streamSessionService.resolveUserContentForRegenerate(
                request.getConversationId(), request.getSourceMessageId());
        SseEmitter emitter = SseEmitterFactory.create(300_000L, response);
        streamSessionService.regenerate(
                request.getConversationId(),
                request.getSourceMessageId(),
                request.getMode(),
                request.getKnowledgeId(),
                emitter);
        return emitter;
    }

    @PostMapping("/api/v1/conversations/messages/list")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "历史消息列表")
    public Result<PageResponse<MessageResponse>> listMessages(@RequestBody MessageListRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.listMessages", request, (req) ->
                messageService.listMessages(req.getId(), req.getPage(), req.getSize()));
    }

    @PostMapping("/api/v1/conversations/messages/before")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "加载更早的消息")
    public Result<MessageListResponse> loadBefore(@Valid @RequestBody MessageLoadBeforeRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.loadBefore", request, (req) ->
                MessageListResponse.builder()
                        .records(messageService.loadMessagesBefore(req.getId(), req.getBefore(), 50))
                        .build());
    }

    @PostMapping("/api/v1/conversations/messages/feedback")
    @SaCheckPermission("conversation:feedback")
    @Operation(summary = "消息反馈")
    public Result<Void> feedback(@Valid @RequestBody MessageFeedbackRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.feedback", request, (req) -> {
            FeedbackType feedbackType = FeedbackType.fromCode(req.getFeedback());
            boolean clear = feedbackType == null || feedbackType == FeedbackType.NONE;
            messageService.updateFeedback(
                    req.getMsgId(),
                    clear ? FeedbackType.NONE : feedbackType,
                    clear ? null : req.getReason());

            if (!clear) {
                Long tenantId = TenantContext.getCurrentTenantId();
                eventPublisher.publishEvent(new MessageFeedbackEvent(
                        this, req.getMsgId(), req.getConversationId(), tenantId,
                        feedbackType, req.getReason()));
            }

            return null;
        });
    }
}
