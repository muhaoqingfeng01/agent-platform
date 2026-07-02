package com.example.agent.interfaces.rest;

import com.example.agent.application.security.SecurityFenceApplicationService;
import com.example.agent.application.security.dto.SensitiveWordCreateCommand;
import com.example.agent.application.security.dto.SecurityEventListResponse;
import com.example.agent.application.security.dto.SecurityEventResponse;
import com.example.agent.application.security.dto.SensitiveWordListResponse;
import com.example.agent.application.security.dto.SensitiveWordResponse;
import com.example.agent.application.security.dto.SensitiveWordUpdateCommand;
import com.example.agent.common.helper.ResultRespHelper;
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
        return ResultRespHelper.responseInvoke("SecurityFenceController.create", request, (req) -> {
            SensitiveWordCreateCommand appReq = SensitiveWordCreateCommand.builder()
                    .word(req.getWord())
                    .matchType(req.getMatchType())
                    .category(req.getCategory())
                    .severity(req.getSeverity())
                    .action(req.getAction())
                    .build();
            return securityService.createSensitiveWord(appReq);
        });
    }

    @PostMapping("/sensitive-words/update")
    @Operation(summary = "更新敏感词规则")
    public Result<SensitiveWordResponse> update(
            @Valid @RequestBody SensitiveWordUpdateRequest request) {
        return ResultRespHelper.responseInvoke("SecurityFenceController.update", request, (req) -> {
            SensitiveWordUpdateCommand appReq = SensitiveWordUpdateCommand.builder()
                    .word(req.getWord())
                    .matchType(req.getMatchType())
                    .category(req.getCategory())
                    .severity(req.getSeverity())
                    .action(req.getAction())
                    .build();
            return securityService.updateSensitiveWord(req.getId(), appReq);
        });
    }

    @PostMapping("/sensitive-words/list")
    @Operation(summary = "敏感词规则列表")
    public Result<SensitiveWordListResponse> list(@RequestBody SensitiveWordListRequest request) {
        return ResultRespHelper.responseInvoke("SecurityFenceController.list", request, (req) ->
                SensitiveWordListResponse.builder()
                        .records(securityService.listSensitiveWords(req.getPage(), req.getSize()))
                        .build());
    }

    @PostMapping("/sensitive-words/get")
    @Operation(summary = "敏感词规则详情")
    public Result<SensitiveWordResponse> getById(@Valid @RequestBody SensitiveWordGetRequest request) {
        return ResultRespHelper.responseInvoke("SecurityFenceController.getById", request, (req) ->
                securityService.getSensitiveWord(req.getId()));
    }

    @PostMapping("/sensitive-words/toggle-status")
    @Operation(summary = "启用/禁用敏感词规则")
    public Result<Void> toggleStatus(@Valid @RequestBody SensitiveWordGetRequest request) {
        return ResultRespHelper.responseInvoke("SecurityFenceController.toggleStatus", request, (req) -> {
            securityService.toggleSensitiveWordStatus(req.getId());
            return null;
        });
    }

    @PostMapping("/sensitive-words/delete")
    @Operation(summary = "删除敏感词规则")
    public Result<Void> delete(@Valid @RequestBody SensitiveWordGetRequest request) {
        return ResultRespHelper.responseInvoke("SecurityFenceController.delete", request, (req) -> {
            securityService.deleteSensitiveWord(req.getId());
            return null;
        });
    }

    @PostMapping("/events/list")
    @Operation(summary = "安全事件列表")
    public Result<SecurityEventListResponse> listEvents(@RequestBody SecurityEventListRequest request) {
        return ResultRespHelper.responseInvoke("SecurityFenceController.listEvents", request, (req) ->
                SecurityEventListResponse.builder()
                        .records(securityService.listSecurityEvents(req.getPage(), req.getSize()))
                        .build());
    }

    @PostMapping("/events/by-conversation")
    @Operation(summary = "按会话查询安全事件")
    public Result<SecurityEventListResponse> listEventsByConversation(
            @Valid @RequestBody SecurityEventListByConversationRequest request) {
        return ResultRespHelper.responseInvoke("SecurityFenceController.listEventsByConversation", request, (req) ->
                SecurityEventListResponse.builder()
                        .records(securityService.listSecurityEventsByConversation(req.getConversationId(), req.getPage(), req.getSize()))
                        .build());
    }
}
