package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.agent.application.tenant.TenantApplicationService;
import com.example.agent.common.result.Result;
import com.example.agent.application.tenant.TenantCreateCommand;
import com.example.agent.application.tenant.TenantResponse;
import com.example.agent.interfaces.dto.request.tenant.TenantListRequest;
import com.example.agent.interfaces.dto.request.tenant.TenantGetRequest;
import com.example.agent.interfaces.dto.request.tenant.TenantUpdateRequest;
import com.example.agent.interfaces.dto.request.tenant.TenantToggleStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理 Controller — 纯粹 HTTP 适配层.
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

    @PostMapping("/create")
    @SaCheckPermission("tenant:write")
    @Operation(summary = "创建租户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "409", description = "租户标识已存在", content = @Content)
    })
    public Result<TenantResponse> create(@Valid @RequestBody TenantCreateCommand request) {
        return Result.ok(tenantService.createTenant(request));
    }

    @PostMapping("/list")
    @SaCheckPermission("tenant:read")
    @Operation(summary = "租户列表")
    public Result<java.util.List<TenantResponse>> list(@RequestBody TenantListRequest request) {
        return Result.ok(tenantService.listTenants(request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("tenant:read")
    @Operation(summary = "租户详情")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "租户不存在", content = @Content))
    public Result<TenantResponse> get(@Valid @RequestBody TenantGetRequest request) {
        return Result.ok(tenantService.getTenant(request.getId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("tenant:write")
    @Operation(summary = "更新租户")
    public Result<TenantResponse> update(@Valid @RequestBody TenantUpdateRequest request) {
        com.example.agent.application.tenant.TenantUpdateCommand updateReq =
                new com.example.agent.application.tenant.TenantUpdateCommand();
        updateReq.setTenantId(request.getTenantId());
        updateReq.setName(request.getName());
        updateReq.setTier(request.getTier());
        updateReq.setConfigJson(request.getConfigJson());
        return Result.ok(tenantService.updateTenant(request.getTenantId(), updateReq));
    }

    @PostMapping("/toggle-status")
    @SaCheckRole("TENANT_ADMIN")
    @Operation(summary = "启停租户")
    public Result<TenantResponse> toggleStatus(@Valid @RequestBody TenantToggleStatusRequest request) {
        com.example.agent.application.tenant.TenantUpdateStatusCommand statusReq =
                new com.example.agent.application.tenant.TenantUpdateStatusCommand();
        statusReq.setStatus(request.getStatus());
        return Result.ok(tenantService.toggleStatus(request.getId(), statusReq));
    }

    @PostMapping("/delete")
    @SaCheckRole("TENANT_ADMIN")
    @Operation(summary = "删除租户（逻辑删除）")
    public Result<Void> delete(@Valid @RequestBody TenantGetRequest request) {
        tenantService.deleteTenant(request.getId());
        return Result.ok();
    }
}
