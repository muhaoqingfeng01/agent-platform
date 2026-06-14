package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.prompt.PromptApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.prompt.CreatePromptRequest;
import com.example.agent.interfaces.dto.request.prompt.PreviewRenderRequest;
import com.example.agent.interfaces.dto.request.prompt.UpdatePromptRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提示词管理 Controller — 纯粹 HTTP 适配层.
 * <p>
 * 不包含任何业务逻辑，仅做:
 * <ul>
 *   <li>参数校验（@Valid）</li>
 *   <li>权限校验（@SaCheckPermission）</li>
 *   <li>请求/响应转换</li>
 *   <li>委托给 {@link PromptApplicationService}</li>
 * </ul>
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

    // ==================== CRUD ====================

    @PostMapping
    @SaCheckPermission("prompt:create")
    @Operation(summary = "创建提示词模板（草稿）")
    public Result<PromptApplicationService.PromptResponse> create(
            @Valid @RequestBody CreatePromptRequest request) {
        PromptApplicationService.CreatePromptRequest appReq =
                new PromptApplicationService.CreatePromptRequest();
        appReq.setName(request.getName());
        appReq.setDescription(request.getDescription());
        appReq.setTemplateText(request.getTemplateText());
        appReq.setVariables(request.getVariables());
        appReq.setAbTestConfig(request.getAbTestConfig());

        return Result.ok(applicationService.createPrompt(appReq));
    }

    @GetMapping
    @SaCheckPermission("prompt:read")
    @Operation(summary = "提示词模板列表（分页）")
    public Result<PageResponse<PromptApplicationService.PromptResponse>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "状态过滤: DRAFT/PUBLISHED/ARCHIVED")
            @RequestParam(required = false) String status) {
        return Result.ok(applicationService.listPrompts(page, size, status));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "模板详情")
    public Result<PromptApplicationService.PromptResponse> getById(
            @Parameter(description = "模板 promptId") @PathVariable String id) {
        return Result.ok(applicationService.getPrompt(id));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("prompt:update")
    @Operation(summary = "编辑模板（仅 DRAFT 状态可编辑）")
    public Result<PromptApplicationService.PromptResponse> update(
            @Parameter(description = "模板 promptId") @PathVariable String id,
            @Valid @RequestBody UpdatePromptRequest request) {
        PromptApplicationService.UpdatePromptRequest appReq =
                new PromptApplicationService.UpdatePromptRequest();
        appReq.setName(request.getName());
        appReq.setDescription(request.getDescription());
        appReq.setTemplateText(request.getTemplateText());
        appReq.setVariables(request.getVariables());

        return Result.ok(applicationService.updatePrompt(id, appReq));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("prompt:delete")
    @Operation(summary = "软删除模板")
    public Result<Void> delete(
            @Parameter(description = "模板 promptId") @PathVariable String id) {
        applicationService.deletePrompt(id);
        return Result.ok();
    }

    // ==================== 预览渲染 ====================

    @PostMapping("/{id}/preview")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "变量填充预览渲染")
    public Result<String> preview(
            @Parameter(description = "模板 promptId") @PathVariable String id,
            @Valid @RequestBody PreviewRenderRequest request) {
        String rendered = applicationService.previewRender(id, request.getVariables());
        return Result.ok(rendered);
    }

    // ==================== 发布与回滚 ====================

    @PostMapping("/{id}/publish")
    @SaCheckPermission("prompt:publish")
    @Operation(summary = "发布当前草稿（版本号 +1）")
    public Result<PromptApplicationService.PromptResponse> publish(
            @Parameter(description = "模板 promptId") @PathVariable String id) {
        return Result.ok(applicationService.publishPrompt(id));
    }

    @PostMapping("/{id}/rollback")
    @SaCheckPermission("prompt:publish")
    @Operation(summary = "回滚到指定版本")
    public Result<PromptApplicationService.PromptResponse> rollback(
            @Parameter(description = "模板 promptId") @PathVariable String id,
            @Parameter(description = "目标版本号", required = true)
            @RequestParam int version) {
        return Result.ok(applicationService.rollbackPrompt(id, version));
    }

    @PostMapping("/{id}/archive")
    @SaCheckPermission("prompt:update")
    @Operation(summary = "归档模板")
    public Result<Void> archive(
            @Parameter(description = "模板 promptId") @PathVariable String id) {
        applicationService.archivePrompt(id);
        return Result.ok();
    }

    // ==================== 版本历史 ====================

    @GetMapping("/{id}/versions")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "查看版本历史列表")
    public Result<List<PromptApplicationService.VersionResponse>> versions(
            @Parameter(description = "模板 promptId") @PathVariable String id) {
        return Result.ok(applicationService.getVersionHistory(id));
    }

    @GetMapping("/{id}/versions/{version}")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "查看指定版本内容")
    public Result<PromptApplicationService.VersionResponse> versionDetail(
            @Parameter(description = "模板 promptId") @PathVariable String id,
            @Parameter(description = "版本号") @PathVariable int version) {
        return Result.ok(applicationService.getVersionDetail(id, version));
    }

    @PostMapping("/{id}/diff")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "两个版本的差异对比")
    public Result<PromptApplicationService.DiffResponse> diff(
            @Parameter(description = "模板 promptId") @PathVariable String id,
            @Parameter(description = "版本1") @RequestParam int v1,
            @Parameter(description = "版本2") @RequestParam int v2) {
        return Result.ok(applicationService.diffVersions(id, v1, v2));
    }

    // ==================== 运行时渲染（编排引擎调用） ====================

    @PostMapping("/{id}/render")
    @SaCheckPermission("prompt:read")
    @Operation(summary = "运行时渲染（仅已发布模板，供编排引擎调用）")
    public Result<String> runtimeRender(
            @Parameter(description = "模板 promptId") @PathVariable String id,
            @Valid @RequestBody PreviewRenderRequest request) {
        String rendered = applicationService.runtimeRender(id, request.getVariables());
        return Result.ok(rendered);
    }
}
