package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.prompt.PromptApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.prompt.PromptCreateRequest;
import com.example.agent.interfaces.dto.request.prompt.PromptListRequest;
import com.example.agent.interfaces.dto.request.prompt.PromptGetRequest;
import com.example.agent.interfaces.dto.request.prompt.PromptUpdateRequest;
import com.example.agent.interfaces.dto.request.prompt.PromptPreviewRenderRequest;
import com.example.agent.interfaces.dto.request.prompt.PromptRollbackRequest;
import com.example.agent.interfaces.dto.request.prompt.PromptVersionDetailRequest;
import com.example.agent.interfaces.dto.request.prompt.PromptDiffVersionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提示词管理 Controller — 纯粹 HTTP 适配层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
@Tag(name = "提示词管理", description = "提示词模板 CRUD、版本发布与回滚、运行时渲染")
public class PromptController {

    private final PromptApplicationService applicationService;

    @PostMapping("/create")
    @SaCheckPermission("prompt:create")
    @Operation(summary = "创建提示词模板（草稿）")
    public Result<PromptApplicationService.PromptResponse> create(
            @Valid @RequestBody PromptCreateRequest request) {
        PromptApplicationService.CreatePromptRequest appReq =
                new PromptApplicationService.CreatePromptRequest();
        appReq.setName(request.getName());
        appReq.setDescription(request.getDescription());
        appReq.setTemplateText(request.getTemplateText());
        appReq.setVariables(request.getVariables());
        appReq.setAbTestConfig(request.getAbTestConfig());
        return Result.ok(applicationService.createPrompt(appReq));
    }

    @PostMapping("/list")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "提示词模板列表（分页）")
    public Result<PageResponse<PromptApplicationService.PromptResponse>> list(
            @RequestBody PromptListRequest request) {
        return Result.ok(applicationService.listPrompts(request.getPage(), request.getSize(), request.getStatus()));
    }

    @PostMapping("/get")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "模板详情")
    public Result<PromptApplicationService.PromptResponse> getById(@Valid @RequestBody PromptGetRequest request) {
        return Result.ok(applicationService.getPrompt(request.getId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("prompt:update")
    @Operation(summary = "编辑模板（仅 DRAFT 状态可编辑）")
    public Result<PromptApplicationService.PromptResponse> update(@Valid @RequestBody PromptUpdateRequest request) {
        PromptApplicationService.UpdatePromptRequest appReq =
                new PromptApplicationService.UpdatePromptRequest();
        appReq.setName(request.getName());
        appReq.setDescription(request.getDescription());
        appReq.setTemplateText(request.getTemplateText());
        appReq.setVariables(request.getVariables());
        return Result.ok(applicationService.updatePrompt(request.getId(), appReq));
    }

    @PostMapping("/delete")
    @SaCheckPermission("prompt:delete")
    @Operation(summary = "软删除模板")
    public Result<Void> delete(@Valid @RequestBody PromptGetRequest request) {
        applicationService.deletePrompt(request.getId());
        return Result.ok();
    }

    @PostMapping("/preview")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "变量填充预览渲染")
    public Result<String> preview(@Valid @RequestBody PromptPreviewRenderRequest request) {
        String rendered = applicationService.previewRender(request.getId(), request.getVariables());
        return Result.ok(rendered);
    }

    @PostMapping("/publish")
    @SaCheckPermission("prompt:publish")
    @Operation(summary = "发布当前草稿（版本号 +1）")
    public Result<PromptApplicationService.PromptResponse> publish(@Valid @RequestBody PromptGetRequest request) {
        return Result.ok(applicationService.publishPrompt(request.getId()));
    }

    @PostMapping("/rollback")
    @SaCheckPermission("prompt:publish")
    @Operation(summary = "回滚到指定版本")
    public Result<PromptApplicationService.PromptResponse> rollback(@Valid @RequestBody PromptRollbackRequest request) {
        return Result.ok(applicationService.rollbackPrompt(request.getId(), request.getVersion()));
    }

    @PostMapping("/archive")
    @SaCheckPermission("prompt:update")
    @Operation(summary = "归档模板")
    public Result<Void> archive(@Valid @RequestBody PromptGetRequest request) {
        applicationService.archivePrompt(request.getId());
        return Result.ok();
    }

    @PostMapping("/versions/list")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "查看版本历史列表")
    public Result<List<PromptApplicationService.VersionResponse>> versions(@Valid @RequestBody PromptGetRequest request) {
        return Result.ok(applicationService.getVersionHistory(request.getId()));
    }

    @PostMapping("/versions/detail")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "查看指定版本内容")
    public Result<PromptApplicationService.VersionResponse> versionDetail(@Valid @RequestBody PromptVersionDetailRequest request) {
        return Result.ok(applicationService.getVersionDetail(request.getId(), request.getVersion()));
    }

    @PostMapping("/diff")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "两个版本的差异对比")
    public Result<PromptApplicationService.DiffResponse> diff(@Valid @RequestBody PromptDiffVersionRequest request) {
        return Result.ok(applicationService.diffVersions(request.getId(), request.getV1(), request.getV2()));
    }

    @PostMapping("/render")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "运行时渲染（仅已发布模板，供编排引擎调用）")
    public Result<String> runtimeRender(@Valid @RequestBody PromptPreviewRenderRequest request) {
        String rendered = applicationService.runtimeRender(request.getId(), request.getVariables());
        return Result.ok(rendered);
    }
}
