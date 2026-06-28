package com.example.agent.interfaces.rest;

import com.example.agent.application.security.SecurityFenceApplicationService;
import com.example.agent.application.security.dto.SensitiveWordCreateCommand;
import com.example.agent.application.security.dto.SecurityEventResponse;
import com.example.agent.application.security.dto.SensitiveWordResponse;
import com.example.agent.application.security.dto.SensitiveWordUpdateCommand;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.security.SensitiveWordCreateRequest;
import com.example.agent.interfaces.dto.request.security.SensitiveWordUpdateRequest;
import com.example.agent.interfaces.dto.request.security.SensitiveWordListRequest;
import com.example.agent.interfaces.dto.request.security.SensitiveWordGetRequest;
import com.example.agent.interfaces.dto.request.security.SecurityEventListRequest;
import com.example.agent.interfaces.dto.request.security.SecurityEventListByConversationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 安全围栏管理控制器 — 敏感词规则 CRUD + 安全事件查询.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
@Tag(name = "安全围栏", description = "敏感词管理与安全事件审计")
public class SecurityFenceController {

    private final SecurityFenceApplicationService securityService;

    @PostMapping("/sensitive-words/create")
    @Operation(summary = "创建敏感词规则")
    public Result<SensitiveWordResponse> create(
            @Valid @RequestBody SensitiveWordCreateRequest request) {
        SensitiveWordCreateCommand appReq = SensitiveWordCreateCommand.builder()
                .word(request.getWord())
                .matchType(request.getMatchType())
                .category(request.getCategory())
                .severity(request.getSeverity())
                .action(request.getAction())
                .build();
        return Result.ok(securityService.createSensitiveWord(appReq));
    }

    @PostMapping("/sensitive-words/update")
    @Operation(summary = "更新敏感词规则")
    public Result<SensitiveWordResponse> update(
            @Valid @RequestBody SensitiveWordUpdateRequest request) {
        SensitiveWordUpdateCommand appReq = SensitiveWordUpdateCommand.builder()
                .word(request.getWord())
                .matchType(request.getMatchType())
                .category(request.getCategory())
                .severity(request.getSeverity())
                .action(request.getAction())
                .build();
        return Result.ok(securityService.updateSensitiveWord(request.getId(), appReq));
    }

    @PostMapping("/sensitive-words/list")
    @Operation(summary = "敏感词规则列表")
    public Result<List<SensitiveWordResponse>> list(@RequestBody SensitiveWordListRequest request) {
        return Result.ok(securityService.listSensitiveWords(request.getPage(), request.getSize()));
    }

    @PostMapping("/sensitive-words/get")
    @Operation(summary = "敏感词规则详情")
    public Result<SensitiveWordResponse> getById(@Valid @RequestBody SensitiveWordGetRequest request) {
        return Result.ok(securityService.getSensitiveWord(request.getId()));
    }

    @PostMapping("/sensitive-words/toggle-status")
    @Operation(summary = "启用/禁用敏感词规则")
    public Result<Void> toggleStatus(@Valid @RequestBody SensitiveWordGetRequest request) {
        securityService.toggleSensitiveWordStatus(request.getId());
        return Result.ok();
    }

    @PostMapping("/sensitive-words/delete")
    @Operation(summary = "删除敏感词规则")
    public Result<Void> delete(@Valid @RequestBody SensitiveWordGetRequest request) {
        securityService.deleteSensitiveWord(request.getId());
        return Result.ok();
    }

    @PostMapping("/events/list")
    @Operation(summary = "安全事件列表")
    public Result<List<SecurityEventResponse>> listEvents(@RequestBody SecurityEventListRequest request) {
        return Result.ok(securityService.listSecurityEvents(request.getPage(), request.getSize()));
    }

    @PostMapping("/events/by-conversation")
    @Operation(summary = "按会话查询安全事件")
    public Result<List<SecurityEventResponse>> listEventsByConversation(
            @Valid @RequestBody SecurityEventListByConversationRequest request) {
        return Result.ok(securityService.listSecurityEventsByConversation(
                request.getConversationId(), request.getPage(), request.getSize()));
    }
}
