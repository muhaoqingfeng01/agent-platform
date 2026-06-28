package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.intent.IntentApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.conversation.valueobject.IntentStatus;
import com.example.agent.interfaces.dto.request.intent.IntentCreateRequest;
import com.example.agent.interfaces.dto.request.intent.IntentListRequest;
import com.example.agent.interfaces.dto.request.intent.IntentGetRequest;
import com.example.agent.interfaces.dto.request.intent.IntentUpdateRequest;
import com.example.agent.interfaces.dto.request.intent.IntentToggleStatusRequest;
import com.example.agent.interfaces.dto.request.intent.IntentTestRequest;
import com.example.agent.interfaces.dto.request.intent.IntentBatchTestRequest;
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

    @PostMapping("/create")
    @SaCheckPermission("intent:create")
    @Operation(summary = "创建意图")
    public Result<IntentApplicationService.IntentResponse> create(
            @Valid @RequestBody IntentCreateRequest request) {
        IntentApplicationService.CreateIntentRequest appReq = toAppRequest(request);
        return Result.ok(intentService.createIntent(appReq));
    }

    @PostMapping("/list")
    @SaCheckPermission("intent:read")
    @Operation(summary = "意图列表")
    public Result<PageResponse<IntentApplicationService.IntentResponse>> list(
            @RequestBody IntentListRequest request) {
        return Result.ok(intentService.listIntents(request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("intent:read")
    @Operation(summary = "意图详情")
    public Result<IntentApplicationService.IntentResponse> getById(@Valid @RequestBody IntentGetRequest request) {
        return Result.ok(intentService.getIntent(request.getId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("intent:update")
    @Operation(summary = "编辑意图")
    public Result<IntentApplicationService.IntentResponse> update(
            @Valid @RequestBody IntentUpdateRequest request) {
        IntentApplicationService.UpdateIntentRequest appReq = new IntentApplicationService.UpdateIntentRequest();
        appReq.setIntentName(request.getIntentName());
        appReq.setPatterns(request.getPatterns());
        appReq.setExamples(request.getExamples());
        appReq.setLlmPrompt(request.getLlmPrompt());
        appReq.setRequiredParams(request.getRequiredParams());
        appReq.setRiskLevel(request.getRiskLevel());
        return Result.ok(intentService.updateIntent(request.getId(), appReq));
    }

    @PostMapping("/toggle-status")
    @SaCheckPermission("intent:update")
    @Operation(summary = "启停意图")
    public Result<Void> toggleStatus(@Valid @RequestBody IntentToggleStatusRequest request) {
        intentService.toggleStatus(request.getId(), IntentStatus.fromCode(request.getStatus()));
        return Result.ok();
    }

    @PostMapping("/test")
    @SaCheckPermission("intent:read")
    @Operation(summary = "测试意图识别")
    public Result<IntentApplicationService.IntentTestResponse> test(
            @Valid @RequestBody IntentTestRequest request) {
        return Result.ok(intentService.testRecognition(request.getId(), request.getInput()));
    }

    @PostMapping("/batch-test")
    @SaCheckPermission("intent:read")
    @Operation(summary = "批量测试")
    public Result<IntentApplicationService.BatchTestResponse> batchTest(
            @Valid @RequestBody IntentBatchTestRequest request) {
        List<IntentApplicationService.TestItem> items = request.getItems().stream().map(item -> {
            IntentApplicationService.TestItem ti = new IntentApplicationService.TestItem();
            ti.setInput(item.getInput());
            ti.setExpectedIntentCode(item.getExpectedIntentCode());
            return ti;
        }).toList();
        return Result.ok(intentService.batchTest(items));
    }

    @PostMapping("/delete")
    @SaCheckPermission("intent:delete")
    @Operation(summary = "删除意图")
    public Result<Void> delete(@Valid @RequestBody IntentGetRequest request) {
        intentService.deleteIntent(request.getId());
        return Result.ok();
    }

    // ==================== Mappers ====================

    private IntentApplicationService.CreateIntentRequest toAppRequest(IntentCreateRequest request) {
        IntentApplicationService.CreateIntentRequest appReq = new IntentApplicationService.CreateIntentRequest();
        appReq.setIntentCode(IdGenerator.generate("int"));
        appReq.setIntentName(request.getName());
        appReq.setCategory(request.getCategory());
        appReq.setPatterns(request.getPatterns());
        appReq.setExamples(request.getExamples());
        appReq.setLlmPrompt(request.getLlmPrompt());
        appReq.setRequiredParams(request.getRequiredParams());
        appReq.setRiskLevel(request.getRiskLevel());
        return appReq;
    }
}
