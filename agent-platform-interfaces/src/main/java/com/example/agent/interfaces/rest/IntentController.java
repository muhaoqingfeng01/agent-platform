package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.intent.IntentApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.intent.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 意图管理 Controller — 纯粹 HTTP 适配层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/intents")
@RequiredArgsConstructor
@Tag(name = "意图管理", description = "意图定义 CRUD 与识别测试")
public class IntentController {

    private final IntentApplicationService intentService;

    @PostMapping
    @SaCheckPermission("intent:create")
    @Operation(summary = "创建意图")
    public Result<IntentApplicationService.IntentResponse> create(
            @Valid @RequestBody CreateIntentRequest request) {
        IntentApplicationService.CreateIntentRequest appReq = toAppRequest(request);
        return Result.ok(intentService.createIntent(appReq));
    }

    @GetMapping
    @SaCheckPermission("intent:read")
    @Operation(summary = "意图列表")
    public Result<PageResponse<IntentApplicationService.IntentResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(intentService.listIntents(page, size));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("intent:read")
    @Operation(summary = "意图详情")
    public Result<IntentApplicationService.IntentResponse> getById(@PathVariable String id) {
        return Result.ok(intentService.getIntent(id));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("intent:update")
    @Operation(summary = "编辑意图")
    public Result<IntentApplicationService.IntentResponse> update(
            @PathVariable String id, @Valid @RequestBody UpdateIntentRequest request) {
        IntentApplicationService.UpdateIntentRequest appReq = toAppRequest(request);
        return Result.ok(intentService.updateIntent(id, appReq));
    }

    @PutMapping("/{id}/status")
    @SaCheckPermission("intent:update")
    @Operation(summary = "启停意图")
    public Result<Void> toggleStatus(@PathVariable String id,
                                      @Valid @RequestBody ToggleIntentStatusRequest request) {
        intentService.toggleStatus(id, request.getStatus());
        return Result.ok();
    }

    @PostMapping("/{id}/test")
    @SaCheckPermission("intent:read")
    @Operation(summary = "测试意图识别")
    public Result<IntentApplicationService.IntentTestResponse> test(
            @PathVariable String id, @Valid @RequestBody IntentTestRequest request) {
        return Result.ok(intentService.testRecognition(id, request.getInput()));
    }

    @PostMapping("/batch-test")
    @SaCheckPermission("intent:read")
    @Operation(summary = "批量测试")
    public Result<IntentApplicationService.BatchTestResponse> batchTest(
            @Valid @RequestBody BatchTestRequest request) {
        List<IntentApplicationService.TestItem> items = request.getItems().stream().map(item -> {
            IntentApplicationService.TestItem ti = new IntentApplicationService.TestItem();
            ti.setInput(item.getInput());
            ti.setExpectedIntentCode(item.getExpectedIntentCode());
            return ti;
        }).toList();
        return Result.ok(intentService.batchTest(items));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("intent:delete")
    @Operation(summary = "删除意图")
    public Result<Void> delete(@PathVariable String id) {
        intentService.deleteIntent(id);
        return Result.ok();
    }

    // ==================== Mappers ====================

    private IntentApplicationService.CreateIntentRequest toAppRequest(CreateIntentRequest request) {
        IntentApplicationService.CreateIntentRequest appReq = new IntentApplicationService.CreateIntentRequest();
        appReq.setIntentCode(request.getIntentCode());
        appReq.setIntentName(request.getIntentName());
        appReq.setCategory(request.getCategory());
        appReq.setPatterns(request.getPatterns());
        appReq.setExamples(request.getExamples());
        appReq.setLlmPrompt(request.getLlmPrompt());
        appReq.setRequiredParams(request.getRequiredParams());
        appReq.setRiskLevel(request.getRiskLevel());
        return appReq;
    }

    private IntentApplicationService.UpdateIntentRequest toAppRequest(UpdateIntentRequest request) {
        IntentApplicationService.UpdateIntentRequest appReq = new IntentApplicationService.UpdateIntentRequest();
        appReq.setIntentName(request.getIntentName());
        appReq.setPatterns(request.getPatterns());
        appReq.setExamples(request.getExamples());
        appReq.setLlmPrompt(request.getLlmPrompt());
        appReq.setRequiredParams(request.getRequiredParams());
        appReq.setRiskLevel(request.getRiskLevel());
        return appReq;
    }
}
