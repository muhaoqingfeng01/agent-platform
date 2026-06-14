package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.conversation.MessageApplicationService.MessageResponse;
import com.example.agent.application.conversation.StreamOrchestrationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.domain.conversation.valueobject.FeedbackType;
import com.example.agent.infrastructure.config.sse.SseEmitterFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 消息收发 Controller — SSE 流式 + 非流式.
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

    @PostMapping("/api/v1/conversations/{id}/messages")
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（非流式）")
    public Result<MessageResponse> sendMessage(@PathVariable String id,
                                                @Valid @RequestBody SendMessageRequest request) {
        log.info("[Message] 发送: convId={}, len={}", id, request.getContent().length());
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
        SseEmitter emitter = SseEmitterFactory.create(300_000L);
        streamExecutor.submit(() -> streamService.executeStreamPipeline(id, request.getContent(), emitter));
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
                                  @Valid @RequestBody FeedbackRequest request) {
        messageService.updateFeedback(msgId, request.getFeedback());
        return Result.ok();
    }

    // ==================== DTOs ====================

    @Data
    public static class SendMessageRequest {
        @NotBlank
        private String content;
    }

    @Data
    public static class FeedbackRequest {
        @NotNull
        private FeedbackType feedback;
    }
}
