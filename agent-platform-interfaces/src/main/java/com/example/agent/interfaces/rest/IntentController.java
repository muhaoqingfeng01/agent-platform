package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.intent.IntentApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.ResultRespHelper;
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
        return ResultRespHelper.responseInvoke("IntentController.create", request, (req) ->
                intentService.createIntent(toAppRequest(req)));
    }

    @PostMapping("/list")
    @SaCheckPermission("intent:read")
    @Operation(summary = "意图列表")
    public Result<PageResponse<IntentApplicationService.IntentResponse>> list(
            @RequestBody IntentListRequest request) {
        return ResultRespHelper.responseInvoke("IntentController.list", request, (req) ->
                intentService.listIntents(req.getPage(), req.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("intent:read")
    @Operation(summary = "意图详情")
    public Result<IntentApplicationService.IntentResponse> getById(@Valid @RequestBody IntentGetRequest request) {
        return ResultRespHelper.responseInvoke("IntentController.getById", request, (req) ->
                intentService.getIntent(req.getId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("intent:update")
    @Operation(summary = "编辑意图")
    public Result<IntentApplicationService.IntentResponse> update(
            @Valid @RequestBody IntentUpdateRequest request) {
        return ResultRespHelper.responseInvoke("IntentController.update", request, (req) -> {
            IntentApplicationService.UpdateIntentRequest appReq = new IntentApplicationService.UpdateIntentRequest();
            appReq.setIntentName(req.getIntentName());
            appReq.setPatterns(req.getPatterns());
            appReq.setExamples(req.getExamples());
            appReq.setLlmPrompt(req.getLlmPrompt());
            appReq.setRequiredParams(req.getRequiredParams());
            appReq.setRiskLevel(req.getRiskLevel());
            return intentService.updateIntent(req.getId(), appReq);
        });
    }

    @PostMapping("/toggle-status")
    @SaCheckPermission("intent:update")
    @Operation(summary = "启停意图")
    public Result<Void> toggleStatus(@Valid @RequestBody IntentToggleStatusRequest request) {
        return ResultRespHelper.responseInvoke("IntentController.toggleStatus", request, (req) -> {
            intentService.toggleStatus(req.getId(), IntentStatus.fromCode(req.getStatus()));
            return null;
        });
    }

    @PostMapping("/test")
    @SaCheckPermission("intent:read")
    @Operation(summary = "测试意图识别")
    public Result<IntentApplicationService.IntentTestResponse> test(
            @Valid @RequestBody IntentTestRequest request) {
        return ResultRespHelper.responseInvoke("IntentController.test", request, (req) ->
                intentService.testRecognition(req.getId(), req.getInput()));
    }

    @PostMapping("/batch-test")
    @SaCheckPermission("intent:read")
    @Operation(summary = "批量测试")
    public Result<IntentApplicationService.BatchTestResponse> batchTest(
            @Valid @RequestBody IntentBatchTestRequest request) {
        return ResultRespHelper.responseInvoke("IntentController.batchTest", request, (req) -> {
            List<IntentApplicationService.TestItem> items = req.getItems().stream().map(item -> {
                IntentApplicationService.TestItem ti = new IntentApplicationService.TestItem();
                ti.setInput(item.getInput());
                ti.setExpectedIntentCode(item.getExpectedIntentCode());
                return ti;
            }).toList();
            return intentService.batchTest(items);
        });
    }

    @PostMapping("/delete")
    @SaCheckPermission("intent:delete")
    @Operation(summary = "删除意图")
    public Result<Void> delete(@Valid @RequestBody IntentGetRequest request) {
        return ResultRespHelper.responseInvoke("IntentController.delete", request, (req) -> {
            intentService.deleteIntent(req.getId());
            return null;
        });
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
