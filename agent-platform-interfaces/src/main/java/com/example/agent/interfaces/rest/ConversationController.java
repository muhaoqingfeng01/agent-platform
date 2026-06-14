package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.conversation.ConversationApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 会话管理 Controller.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Tag(name = "对话管理", description = "会话 CRUD 与状态管理")
public class ConversationController {

    private final ConversationApplicationService applicationService;

    @PostMapping
    @SaCheckPermission("conversation:create")
    @Operation(summary = "创建新会话")
    public Result<ConversationApplicationService.ConversationResponse> create(
            @Valid @RequestBody CreateConversationRequest request) {
        log.info("[Conversation] 创建: agentId={}", request.getAgentId());
        return Result.ok(applicationService.createConversation(toAppRequest(request)));
    }

    @GetMapping
    @SaCheckPermission("conversation:read")
    @Operation(summary = "我的会话列表")
    public Result<PageResponse<ConversationApplicationService.ConversationResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(applicationService.listConversations(page, size));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "会话详情")
    public Result<ConversationApplicationService.ConversationResponse> getById(@PathVariable String id) {
        return Result.ok(ConversationApplicationService.ConversationResponse.from(
                applicationService.getConversation(id)));
    }

    @PatchMapping("/{id}/title")
    @SaCheckPermission("conversation:update")
    @Operation(summary = "更新标题")
    public Result<Void> updateTitle(@PathVariable String id,
                                     @Valid @RequestBody UpdateTitleRequest request) {
        applicationService.updateTitle(id, request.getTitle());
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    @SaCheckPermission("conversation:update")
    @Operation(summary = "状态流转")
    public Result<Void> transitionStatus(@PathVariable String id,
                                          @Valid @RequestBody TransitionStatusRequest request) {
        applicationService.transitionStatus(id, request.getTargetStatus());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("conversation:delete")
    @Operation(summary = "逻辑删除")
    public Result<Void> delete(@PathVariable String id) {
        applicationService.softDelete(id);
        return Result.ok();
    }

    private ConversationApplicationService.CreateConversationRequest toAppRequest(
            CreateConversationRequest request) {
        ConversationApplicationService.CreateConversationRequest appReq =
                new ConversationApplicationService.CreateConversationRequest();
        appReq.setAgentId(request.getAgentId());
        appReq.setTitle(request.getTitle());
        appReq.setMetadata(request.getMetadata());
        return appReq;
    }

    // ==================== DTOs ====================

    @Data
    public static class CreateConversationRequest {
        @NotBlank
        private String agentId;
        private String title;
        private Map<String, Object> metadata;
    }

    @Data
    public static class UpdateTitleRequest {
        @NotBlank
        private String title;
    }

    @Data
    public static class TransitionStatusRequest {
        @NotNull
        private ConversationStatus targetStatus;
    }
}
