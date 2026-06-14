package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.agent.application.tenant.TenantApplicationService;
import com.example.agent.common.result.Result;
import com.example.agent.application.tenant.CreateTenantRequest;
import com.example.agent.application.tenant.UpdateTenantRequest;
import com.example.agent.application.tenant.UpdateTenantStatusRequest;
import com.example.agent.application.tenant.TenantResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理 Controller — 纯粹 HTTP 适配层，业务逻辑委托给 {@link TenantApplicationService}.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "租户管理", description = "租户 CRUD、启停操作")
public class TenantController {

    private final TenantApplicationService tenantService;

    @PostMapping
    @SaCheckPermission("tenant:write")
    @Operation(summary = "创建租户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "409", description = "租户标识已存在", content = @Content)
    })
    public Result<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
        return Result.ok(tenantService.createTenant(request));
    }

    @GetMapping
    @SaCheckPermission("tenant:read")
    @Operation(summary = "租户列表")
    public Result<List<TenantResponse>> list(
            @Parameter(description = "页码（从 0 开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.ok(tenantService.listTenants(page, size));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("tenant:read")
    @Operation(summary = "租户详情")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "租户不存在", content = @Content))
    public Result<TenantResponse> get(
            @Parameter(description = "租户主键 ID") @PathVariable Long id) {
        return Result.ok(tenantService.getTenant(id));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("tenant:write")
    @Operation(summary = "更新租户")
    public Result<TenantResponse> update(
            @Parameter(description = "租户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest request) {
        return Result.ok(tenantService.updateTenant(id, request));
    }

    @PatchMapping("/{id}/status")
    @SaCheckRole("TENANT_ADMIN")
    @Operation(summary = "启停租户")
    public Result<TenantResponse> toggleStatus(
            @Parameter(description = "租户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTenantStatusRequest request) {
        return Result.ok(tenantService.toggleStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("TENANT_ADMIN")
    @Operation(summary = "删除租户（逻辑删除）")
    public Result<Void> delete(
            @Parameter(description = "租户主键 ID") @PathVariable Long id) {
        tenantService.deleteTenant(id);
        return Result.ok();
    }
}
