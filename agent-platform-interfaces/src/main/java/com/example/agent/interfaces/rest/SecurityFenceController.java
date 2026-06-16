package com.example.agent.interfaces.rest;

import com.example.agent.application.security.SecurityFenceApplicationService;
import com.example.agent.application.security.dto.CreateSensitiveWordRequest;
import com.example.agent.application.security.dto.SecurityEventResponse;
import com.example.agent.application.security.dto.SensitiveWordResponse;
import com.example.agent.application.security.dto.UpdateSensitiveWordRequest;
import com.example.agent.common.result.Result;
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

    // ==================== 敏感词管理 ====================

    @PostMapping("/sensitive-words")
    @Operation(summary = "创建敏感词规则")
    public Result<SensitiveWordResponse> create(
            @Valid @RequestBody com.example.agent.interfaces.dto.request.security.CreateSensitiveWordRequest request) {
        CreateSensitiveWordRequest appReq = CreateSensitiveWordRequest.builder()
                .word(request.getWord())
                .matchType(request.getMatchType())
                .category(request.getCategory())
                .severity(request.getSeverity())
                .action(request.getAction())
                .build();
        return Result.ok(securityService.createSensitiveWord(appReq));
    }

    @PutMapping("/sensitive-words/{id}")
    @Operation(summary = "更新敏感词规则")
    public Result<SensitiveWordResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody com.example.agent.interfaces.dto.request.security.UpdateSensitiveWordRequest request) {
        UpdateSensitiveWordRequest appReq = UpdateSensitiveWordRequest.builder()
                .word(request.getWord())
                .matchType(request.getMatchType())
                .category(request.getCategory())
                .severity(request.getSeverity())
                .action(request.getAction())
                .build();
        return Result.ok(securityService.updateSensitiveWord(id, appReq));
    }

    @GetMapping("/sensitive-words")
    @Operation(summary = "敏感词规则列表")
    public Result<List<SensitiveWordResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SensitiveWordResponse> list = securityService.listSensitiveWords(page, size);
        return Result.ok(list);
    }

    @GetMapping("/sensitive-words/{id}")
    @Operation(summary = "敏感词规则详情")
    public Result<SensitiveWordResponse> getById(@PathVariable Long id) {
        return Result.ok(securityService.getSensitiveWord(id));
    }

    @PutMapping("/sensitive-words/{id}/toggle-status")
    @Operation(summary = "启用/禁用敏感词规则")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        securityService.toggleSensitiveWordStatus(id);
        return Result.ok();
    }

    @DeleteMapping("/sensitive-words/{id}")
    @Operation(summary = "删除敏感词规则")
    public Result<Void> delete(@PathVariable Long id) {
        securityService.deleteSensitiveWord(id);
        return Result.ok();
    }

    // ==================== 安全事件查询 ====================

    @GetMapping("/events")
    @Operation(summary = "安全事件列表")
    public Result<List<SecurityEventResponse>> listEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(securityService.listSecurityEvents(page, size));
    }

    @GetMapping("/events/conversation/{conversationId}")
    @Operation(summary = "按会话查询安全事件")
    public Result<List<SecurityEventResponse>> listEventsByConversation(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(securityService.listSecurityEventsByConversation(conversationId, page, size));
    }
}
