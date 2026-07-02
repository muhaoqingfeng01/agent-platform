package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.tool.ToolApplicationService;
import com.example.agent.application.tool.dto.ToolInvocationLogResponse;
import com.example.agent.application.tool.dto.ToolResponse;
import com.example.agent.application.tool.dto.ToolTestResponse;
import com.example.agent.application.tool.dto.VersionListResponse;
import com.example.agent.application.tool.dto.VersionResponse;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.JsonHelper;
import com.example.agent.common.helper.ResultRespHelper;
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
        return ResultRespHelper.responseInvoke("ToolController.create", request, (req) ->
                toolService.create(toAppRequest(req)));
    }

    @PostMapping("/list")
    @SaCheckPermission("tool:read")
    @Operation(summary = "工具列表（按类型筛选）")
    public Result<PageResponse<ToolResponse>> list(@RequestBody ToolListRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.list", request, (req) ->
                toolService.list(req.getPage(), req.getSize(), req.getType()));
    }

    @PostMapping("/invocations")
    @SaCheckPermission("tool:read")
    @Operation(summary = "调用日志列表")
    public Result<PageResponse<ToolInvocationLogResponse>> listInvocations(
            @RequestBody ToolListInvocationRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.listInvocations", request, (req) ->
                toolService.listInvocations(req.getToolId(), req.getPage(), req.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("tool:read")
    @Operation(summary = "工具详情")
    public Result<ToolResponse> getById(@Valid @RequestBody ToolGetRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.getById", request, (req) ->
                toolService.getByToolId(req.getId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("tool:update")
    @Operation(summary = "编辑工具配置")
    public Result<ToolResponse> update(@Valid @RequestBody ToolUpdateRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.update", request, (req) -> {
            com.example.agent.application.tool.dto.ToolUpdateCommand appReq =
                    new com.example.agent.application.tool.dto.ToolUpdateCommand();
            appReq.setName(req.getName());
            appReq.setDescription(req.getDescription());
            appReq.setToolType(req.getToolType());
            appReq.setInputSchema(JsonHelper.toJson(req.getInputSchema()));
            appReq.setOutputSchema(JsonHelper.toJson(req.getOutputSchema()));
            appReq.setEndpoint(req.getEndpoint());
            appReq.setAuthType(req.getAuthType());
            appReq.setApiKey(req.getApiKey());
            appReq.setToken(req.getToken());
            appReq.setRequireApproval(req.isRequireApproval());
            return toolService.update(req.getId(), appReq);
        });
    }

    @PostMapping("/toggle-status")
    @SaCheckPermission("tool:update")
    @Operation(summary = "启停工具")
    public Result<ToolResponse> toggleStatus(@Valid @RequestBody ToolToggleStatusRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.toggleStatus", request, (req) ->
                toolService.toggleStatus(req.getId(), req.getStatus()));
    }

    @PostMapping("/test")
    @SaCheckPermission("tool:read")
    @Operation(summary = "测试工具调用")
    public Result<ToolTestResponse> test(@Valid @RequestBody ToolTestRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.test", request, (req) ->
                toolService.test(req.getId(), req.getParams()));
    }

    @PostMapping("/versions/list")
    @SaCheckPermission("tool:read")
    @Operation(summary = "版本历史列表")
    public Result<VersionListResponse> getVersionHistory(@Valid @RequestBody ToolGetRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.getVersionHistory", request, (req) ->
                VersionListResponse.builder()
                        .records(toolService.getVersionHistory(req.getId()))
                        .build());
    }

    @PostMapping("/versions/detail")
    @SaCheckPermission("tool:read")
    @Operation(summary = "版本详情")
    public Result<VersionResponse> getVersionDetail(@Valid @RequestBody ToolVersionDetailRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.getVersionDetail", request, (req) ->
                toolService.getVersionDetail(req.getId(), req.getVersion()));
    }

    @PostMapping("/rollback")
    @SaCheckPermission("tool:update")
    @Operation(summary = "回滚到指定版本")
    public Result<ToolResponse> rollback(@Valid @RequestBody ToolRollbackRequest request) {
        return ResultRespHelper.responseInvoke("ToolController.rollback", request, (req) ->
                toolService.rollback(req.getId(), req.getVersion()));
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
