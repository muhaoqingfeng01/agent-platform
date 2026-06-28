package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.tool.ToolApplicationService;
import com.example.agent.application.tool.dto.ToolInvocationLogResponse;
import com.example.agent.application.tool.dto.ToolResponse;
import com.example.agent.application.tool.dto.ToolTestResponse;
import com.example.agent.application.tool.dto.VersionResponse;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.JsonHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.tool.ToolCreateRequest;
import com.example.agent.interfaces.dto.request.tool.ToolListRequest;
import com.example.agent.interfaces.dto.request.tool.ToolListInvocationRequest;
import com.example.agent.interfaces.dto.request.tool.ToolGetRequest;
import com.example.agent.interfaces.dto.request.tool.ToolUpdateRequest;
import com.example.agent.interfaces.dto.request.tool.ToolToggleStatusRequest;
import com.example.agent.interfaces.dto.request.tool.ToolRollbackRequest;
import com.example.agent.interfaces.dto.request.tool.ToolVersionDetailRequest;
import com.example.agent.interfaces.dto.request.tool.ToolTestRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工具管理 Controller — 纯粹 HTTP 适配层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tools")
@RequiredArgsConstructor
@Tag(name = "工具平台", description = "MCP 工具注册、调用、适配")
public class ToolController {

    private final ToolApplicationService toolService;

    @PostMapping("/create")
    @SaCheckPermission("tool:create")
    @Operation(summary = "注册新工具")
    public Result<ToolResponse> create(@Valid @RequestBody ToolCreateRequest request) {
        com.example.agent.application.tool.dto.ToolCreateCommand appReq = toAppRequest(request);
        return Result.ok(toolService.create(appReq));
    }

    @PostMapping("/list")
    @SaCheckPermission("tool:read")
    @Operation(summary = "工具列表（按类型筛选）")
    public Result<PageResponse<ToolResponse>> list(@RequestBody ToolListRequest request) {
        return Result.ok(toolService.list(request.getPage(), request.getSize(), request.getType()));
    }

    @PostMapping("/invocations")
    @SaCheckPermission("tool:read")
    @Operation(summary = "调用日志列表")
    public Result<PageResponse<ToolInvocationLogResponse>> listInvocations(
            @RequestBody ToolListInvocationRequest request) {
        return Result.ok(toolService.listInvocations(request.getToolId(), request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("tool:read")
    @Operation(summary = "工具详情")
    public Result<ToolResponse> getById(@Valid @RequestBody ToolGetRequest request) {
        return Result.ok(toolService.getByToolId(request.getId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("tool:update")
    @Operation(summary = "编辑工具配置")
    public Result<ToolResponse> update(@Valid @RequestBody ToolUpdateRequest request) {
        com.example.agent.application.tool.dto.ToolUpdateCommand appReq = new com.example.agent.application.tool.dto.ToolUpdateCommand();
        appReq.setName(request.getName());
        appReq.setDescription(request.getDescription());
        appReq.setToolType(request.getToolType());
        appReq.setInputSchema(JsonHelper.toJson(request.getInputSchema()));
        appReq.setOutputSchema(JsonHelper.toJson(request.getOutputSchema()));
        appReq.setEndpoint(request.getEndpoint());
        appReq.setAuthType(request.getAuthType());
        appReq.setApiKey(request.getApiKey());
        appReq.setToken(request.getToken());
        appReq.setRequireApproval(request.isRequireApproval());
        return Result.ok(toolService.update(request.getId(), appReq));
    }

    @PostMapping("/toggle-status")
    @SaCheckPermission("tool:update")
    @Operation(summary = "启停工具")
    public Result<ToolResponse> toggleStatus(@Valid @RequestBody ToolToggleStatusRequest request) {
        return Result.ok(toolService.toggleStatus(request.getId(), request.getStatus()));
    }

    @PostMapping("/test")
    @SaCheckPermission("tool:read")
    @Operation(summary = "测试工具调用")
    public Result<ToolTestResponse> test(@Valid @RequestBody ToolTestRequest request) {
        return Result.ok(toolService.test(request.getId(), request.getParams()));
    }

    @PostMapping("/versions/list")
    @SaCheckPermission("tool:read")
    @Operation(summary = "版本历史列表")
    public Result<List<VersionResponse>> getVersionHistory(@Valid @RequestBody ToolGetRequest request) {
        return Result.ok(toolService.getVersionHistory(request.getId()));
    }

    @PostMapping("/versions/detail")
    @SaCheckPermission("tool:read")
    @Operation(summary = "版本详情")
    public Result<VersionResponse> getVersionDetail(@Valid @RequestBody ToolVersionDetailRequest request) {
        return Result.ok(toolService.getVersionDetail(request.getId(), request.getVersion()));
    }

    @PostMapping("/rollback")
    @SaCheckPermission("tool:update")
    @Operation(summary = "回滚到指定版本")
    public Result<ToolResponse> rollback(@Valid @RequestBody ToolRollbackRequest request) {
        return Result.ok(toolService.rollback(request.getId(), request.getVersion()));
    }

    // ==================== DTO 映射方法 ====================

    private com.example.agent.application.tool.dto.ToolCreateCommand toAppRequest(ToolCreateRequest dto) {
        com.example.agent.application.tool.dto.ToolCreateCommand appReq =
                new com.example.agent.application.tool.dto.ToolCreateCommand();
        appReq.setName(dto.getName());
        appReq.setDescription(dto.getDescription());
        appReq.setToolType(dto.getToolType());
        appReq.setInputSchema(dto.getInputSchema());
        appReq.setOutputSchema(dto.getOutputSchema());
        appReq.setEndpoint(dto.getEndpoint());
        appReq.setAuthType(dto.getAuthType());
        appReq.setApiKey(dto.getApiKey());
        appReq.setToken(dto.getToken());
        appReq.setRequireApproval(dto.isRequireApproval());
        return appReq;
    }
}
