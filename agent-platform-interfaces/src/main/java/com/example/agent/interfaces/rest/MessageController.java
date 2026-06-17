package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.conversation.MessageApplicationService.MessageResponse;
import com.example.agent.application.conversation.StreamOrchestrationService;
import com.example.agent.application.optimization.event.MessageFeedbackEvent;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.infrastructure.config.sse.SseEmitterFactory;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.interfaces.dto.request.message.MessageFeedbackRequest;
import com.example.agent.interfaces.dto.request.message.SendMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 消息收发 Controller — SSE 流式 + 非流式，纯粹 HTTP 适配层.
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
    private final StreamOrchestrationService streamService;
    private final ThreadPoolExecutor streamExecutor;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/api/v1/conversations/{id}/messages")
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（非流式）")
    public Result<MessageResponse> sendMessage(@PathVariable String id,
                                                @Valid @RequestBody SendMessageRequest request) {
        MessageResponse userMsg = MessageResponse.from(
                messageService.saveUserMessage(id, request.getContent()));
        return Result.ok(userMsg);
    }

    @PostMapping(value = "/api/v1/conversations/{id}/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（SSE 流式）")
    public SseEmitter streamChat(@PathVariable String id,
                                  @Valid @RequestBody SendMessageRequest request) {
        // 在线程池线程执行前捕获 ThreadLocal 上下文
        Long tenantId = TenantContext.getCurrentTenantId();
        String userId = TenantContext.getCurrentUserId();
        SseEmitter emitter = SseEmitterFactory.create(300_000L);
        streamExecutor.submit(() -> streamService.executeStreamPipeline(
                id, tenantId, userId, request.getContent(), emitter));
        return emitter;
    }

    @GetMapping("/api/v1/conversations/{id}/messages")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "历史消息列表")
    public Result<PageResponse<MessageResponse>> listMessages(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return Result.ok(messageService.listMessages(id, page, size));
    }

    @GetMapping(value = "/api/v1/conversations/{id}/messages", params = "before")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "加载更早的消息")
    public Result<List<MessageResponse>> loadBefore(@PathVariable String id,
                                                      @RequestParam String before) {
        return Result.ok(messageService.loadMessagesBefore(id, before, 50));
    }

    @PatchMapping("/api/v1/conversations/{id}/messages/{msgId}/feedback")
    @SaCheckPermission("conversation:feedback")
    @Operation(summary = "消息反馈")
    public Result<Void> feedback(@PathVariable String id,
                                  @PathVariable String msgId,
                                  @Valid @RequestBody MessageFeedbackRequest request) {
        messageService.updateFeedback(msgId, request.getFeedback());

        // 发布反馈事件（BadCase 自动工单监听）
        Long tenantId = TenantContext.getCurrentTenantId();
        eventPublisher.publishEvent(new MessageFeedbackEvent(
                this, msgId, id, tenantId, request.getFeedback()));

        return Result.ok();
    }
}
