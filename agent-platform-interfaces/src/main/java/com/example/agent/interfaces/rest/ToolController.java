package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.tool.ToolApplicationService;
import com.example.agent.application.tool.dto.ToolInvocationLogResponse;
import com.example.agent.application.tool.dto.ToolResponse;
import com.example.agent.application.tool.dto.ToolTestResponse;
import com.example.agent.application.tool.dto.VersionResponse;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.tool.CreateToolRequest;
import com.example.agent.interfaces.dto.request.tool.ToggleToolStatusRequest;
import com.example.agent.interfaces.dto.request.tool.ToolTestRequest;
import com.example.agent.interfaces.dto.request.tool.UpdateToolRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工具管理 Controller — 纯粹 HTTP 适配层.
 *
 * <p>负责 HTTP 请求的接收、参数校验和响应封装，
 * 所有业务逻辑委托给 {@link ToolApplicationService}.
 * 遵循 DDD 分层约束：Controller → ApplicationService → DomainService → Repository.
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

    /** 工具应用服务 — 业务编排入口 */
    private final ToolApplicationService toolService;

    // ==================== 工具 CRUD ====================

    /**
     * 注册新工具.
     *
     * @param request 创建工具请求体
     * @return 创建后的工具详情
     */
    @PostMapping
    @SaCheckPermission("tool:create")
    @Operation(summary = "注册新工具")
    public Result<ToolResponse> create(@Valid @RequestBody CreateToolRequest request) {
        com.example.agent.application.tool.dto.CreateToolRequest appReq = toAppRequest(request);
        return Result.ok(toolService.create(appReq));
    }

    /**
     * 工具列表 — 支持按类型筛选，分页返回.
     *
     * @param page 页码（从 0 开始，默认 0）
     * @param size 每页数量（默认 20）
     * @param type 工具类型筛选（可选，如 MCP、HTTP）
     * @return 分页的工具列表
     */
    @GetMapping
    @SaCheckPermission("tool:read")
    @Operation(summary = "工具列表（按类型筛选）")
    public Result<PageResponse<ToolResponse>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "工具类型（可选）") @RequestParam(required = false) String type) {
        return Result.ok(toolService.list(page, size, type));
    }

    /**
     * 调用日志列表 — 必须在 {@code /{id}} 路由之前定义，避免 Spring MVC 路由歧义.
     *
     * @param toolId 工具 ID（可选，不传则返回全部）
     * @param page   页码
     * @param size   每页数量
     * @return 分页的调用日志列表
     */
    @GetMapping("/invocations")
    @SaCheckPermission("tool:read")
    @Operation(summary = "调用日志列表")
    public Result<PageResponse<ToolInvocationLogResponse>> listInvocations(
            @Parameter(description = "工具 ID（可选）") @RequestParam(required = false) String toolId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.ok(toolService.listInvocations(toolId, page, size));
    }

    /**
     * 工具详情.
     *
     * @param id 工具业务 ID（tool_{snowflake_id}）
     * @return 工具详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("tool:read")
    @Operation(summary = "工具详情")
    public Result<ToolResponse> getById(
            @Parameter(description = "工具 ID") @PathVariable String id) {
        return Result.ok(toolService.getByToolId(id));
    }

    /**
     * 编辑工具配置 — 仅更新传入的非 null 字段.
     *
     * @param id      工具业务 ID
     * @param request 更新请求（全可选字段）
     * @return 更新后的工具详情
     */
    @PutMapping("/{id}")
    @SaCheckPermission("tool:update")
    @Operation(summary = "编辑工具配置")
    public Result<ToolResponse> update(
            @Parameter(description = "工具 ID") @PathVariable String id,
            @Valid @RequestBody UpdateToolRequest request) {
        com.example.agent.application.tool.dto.UpdateToolRequest appReq = toAppRequest(request);
        return Result.ok(toolService.update(id, appReq));
    }

    /**
     * 启停工具 — 切换 ACTIVE / DISABLED 状态.
     *
     * @param id      工具业务 ID
     * @param request 包含目标状态
     * @return 更新后的工具详情
     */
    @PatchMapping("/{id}/status")
    @SaCheckPermission("tool:update")
    @Operation(summary = "启停工具")
    public Result<ToolResponse> toggleStatus(
            @Parameter(description = "工具 ID") @PathVariable String id,
            @Valid @RequestBody ToggleToolStatusRequest request) {
        return Result.ok(toolService.toggleStatus(id, request.getStatus()));
    }

    /**
     * 测试工具调用 — 同步调用并返回结果和耗时.
     *
     * <p>支持 MCP 和 HTTP 类型的工具，BUILTIN/CUSTOM 需通过任务引擎测试.
     *
     * @param id      工具业务 ID
     * @param request 测试参数
     * @return 测试结果（成功/失败 + 返回值 + 耗时 + 调用记录 ID）
     */
    @PostMapping("/{id}/test")
    @SaCheckPermission("tool:read")
    @Operation(summary = "测试工具调用")
    public Result<ToolTestResponse> test(
            @Parameter(description = "工具 ID") @PathVariable String id,
            @Valid @RequestBody ToolTestRequest request) {
        return Result.ok(toolService.test(id, request.getParams()));
    }

    // ==================== 版本历史 ====================

    /**
     * 版本历史列表.
     */
    @GetMapping("/{id}/versions")
    @SaCheckPermission("tool:read")
    @Operation(summary = "版本历史列表")
    public Result<List<VersionResponse>> getVersionHistory(
            @Parameter(description = "工具 ID") @PathVariable String id) {
        return Result.ok(toolService.getVersionHistory(id));
    }

    /**
     * 版本详情.
     */
    @GetMapping("/{id}/versions/{version}")
    @SaCheckPermission("tool:read")
    @Operation(summary = "版本详情")
    public Result<VersionResponse> getVersionDetail(
            @Parameter(description = "工具 ID") @PathVariable String id,
            @Parameter(description = "版本号") @PathVariable int version) {
        return Result.ok(toolService.getVersionDetail(id, version));
    }

    /**
     * 回滚到指定版本.
     */
    @PostMapping("/{id}/rollback")
    @SaCheckPermission("tool:update")
    @Operation(summary = "回滚到指定版本")
    public Result<ToolResponse> rollback(
            @Parameter(description = "工具 ID") @PathVariable String id,
            @Parameter(description = "目标版本号") @RequestParam int version) {
        return Result.ok(toolService.rollback(id, version));
    }

    // ==================== DTO 映射方法 ====================

    /**
     * 将 Interfaces 层 CreateToolRequest 转换为 Application 层 CreateToolRequest.
     */
    private com.example.agent.application.tool.dto.CreateToolRequest toAppRequest(
            CreateToolRequest dto) {
        com.example.agent.application.tool.dto.CreateToolRequest appReq =
                new com.example.agent.application.tool.dto.CreateToolRequest();
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

    /**
     * 将 Interfaces 层 UpdateToolRequest 转换为 Application 层 UpdateToolRequest.
     */
    private com.example.agent.application.tool.dto.UpdateToolRequest toAppRequest(
            UpdateToolRequest dto) {
        com.example.agent.application.tool.dto.UpdateToolRequest appReq =
                new com.example.agent.application.tool.dto.UpdateToolRequest();
        appReq.setName(dto.getName());
        appReq.setDescription(dto.getDescription());
        appReq.setToolType(dto.getToolType());
        appReq.setInputSchema(dto.getInputSchema());
        appReq.setOutputSchema(dto.getOutputSchema());
        appReq.setEndpoint(dto.getEndpoint());
        appReq.setAuthType(dto.getAuthType());
        appReq.setApiKey(dto.getApiKey());
        appReq.setToken(dto.getToken());
        appReq.setRequireApproval(dto.getRequireApproval());
        return appReq;
    }
}
