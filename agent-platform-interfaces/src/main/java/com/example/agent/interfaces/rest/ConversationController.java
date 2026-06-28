package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.conversation.ConversationApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import com.example.agent.interfaces.dto.request.conversation.ConversationCreateRequest;
import com.example.agent.interfaces.dto.request.conversation.ConversationListRequest;
import com.example.agent.interfaces.dto.request.conversation.ConversationGetRequest;
import com.example.agent.interfaces.dto.request.conversation.ConversationUpdateTitleRequest;
import com.example.agent.interfaces.dto.request.conversation.ConversationTransitionStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 会话管理 Controller — 纯粹 HTTP 适配层.
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

    @PostMapping("/create")
    @SaCheckPermission("conversation:create")
    @Operation(summary = "创建新会话")
    public Result<ConversationApplicationService.ConversationResponse> create(
            @Valid @RequestBody ConversationCreateRequest request) {
        return Result.ok(applicationService.createConversation(toAppRequest(request)));
    }

    @PostMapping("/list")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "我的会话列表")
    public Result<PageResponse<ConversationApplicationService.ConversationResponse>> list(
            @RequestBody ConversationListRequest request) {
        return Result.ok(applicationService.listConversations(request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "会话详情")
    public Result<ConversationApplicationService.ConversationResponse> getById(
            @Valid @RequestBody ConversationGetRequest request) {
        return Result.ok(ConversationApplicationService.ConversationResponse.from(
                applicationService.getConversation(request.getId())));
    }

    @PostMapping("/update-title")
    @SaCheckPermission("conversation:update")
    @Operation(summary = "更新标题")
    public Result<Void> updateTitle(@Valid @RequestBody ConversationUpdateTitleRequest request) {
        applicationService.updateTitle(request.getId(), request.getTitle());
        return Result.ok();
    }

    @PostMapping("/transition-status")
    @SaCheckPermission("conversation:update")
    @Operation(summary = "状态流转")
    public Result<Void> transitionStatus(@Valid @RequestBody ConversationTransitionStatusRequest request) {
        applicationService.transitionStatus(request.getId(), ConversationStatus.valueOf(request.getTargetStatus()));
        return Result.ok();
    }

    @PostMapping("/delete")
    @SaCheckPermission("conversation:delete")
    @Operation(summary = "逻辑删除")
    public Result<Void> delete(@Valid @RequestBody ConversationGetRequest request) {
        applicationService.softDelete(request.getId());
        return Result.ok();
    }

    private ConversationApplicationService.CreateConversationRequest toAppRequest(
            ConversationCreateRequest request) {
        ConversationApplicationService.CreateConversationRequest appReq =
                new ConversationApplicationService.CreateConversationRequest();
        appReq.setAgentId(request.getAgentId());
        appReq.setTitle(request.getTitle());
        appReq.setMetadata(request.getMetadata());
        return appReq;
    }
}
