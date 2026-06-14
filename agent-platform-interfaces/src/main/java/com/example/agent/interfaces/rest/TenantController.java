package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.agent.common.result.Result;
import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "租户管理", description = "租户 CRUD、启停操作")
public class TenantController {

    private final TenantRepository tenantRepository;

    // ==================== DTOs ====================

    @Getter @Setter
    public static class CreateTenantRequest {
        @NotBlank @Size(min = 2, max = 64)
        @Schema(description = "租户唯一标识", example = "tenant_acme")
        private String tenantId;
        @NotBlank @Size(min = 2, max = 128)
        @Schema(description = "租户名称", example = "ACME 科技有限公司")
        private String name;
        @Schema(description = "套餐等级", example = "STANDARD", allowableValues = {"STANDARD", "PREMIUM", "ENTERPRISE"})
        private String tier = "STANDARD";
    }

    @Getter @Setter
    public static class UpdateTenantRequest {
        @NotBlank @Size(min = 2, max = 128)
        @Schema(description = "租户名称", example = "ACME 科技有限公司（更新）")
        private String name;
        @Schema(description = "套餐等级", example = "PREMIUM")
        private String tier;
        @Schema(description = "租户配置 JSON", example = "{\"maxUsers\":100}")
        private String configJson;
    }

    @Getter @Setter
    public static class UpdateTenantStatusRequest {
        @NotBlank
        @Schema(description = "目标状态", example = "SUSPENDED", allowableValues = {"ACTIVE", "SUSPENDED"})
        private String status;
    }

    @Getter @Setter
    public static class TenantResponse {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "租户唯一标识")
        private String tenantId;
        @Schema(description = "租户名称")
        private String name;
        @Schema(description = "状态")
        private String status;
        @Schema(description = "套餐等级")
        private String tier;
        @Schema(description = "配置 JSON")
        private String configJson;
        @Schema(description = "创建时间")
        private LocalDateTime createdAt;

        public static TenantResponse from(Tenant t) {
            TenantResponse r = new TenantResponse();
            r.id = t.getId(); r.tenantId = t.getTenantId(); r.name = t.getName();
            r.status = t.getStatus(); r.tier = t.getTier();
            r.configJson = t.getConfigJson(); r.createdAt = t.getCreatedAt();
            return r;
        }
    }

    // ==================== API ====================

    @PostMapping
    @SaCheckPermission("tenant:write")
    @Operation(summary = "创建租户", description = "创建新租户，需 tenant:write 权限")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "409", description = "租户标识已存在", content = @Content)
    })
    public Result<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
        log.info("[Tenant] 创建租户: tenantId={}, name={}", request.getTenantId(), request.getName());
        // 检查重复
        if (tenantRepository.findByTenantId(request.getTenantId()).isPresent()) {
            return Result.conflict("租户标识已存在: " + request.getTenantId());
        }
        Tenant tenant = Tenant.builder()
                .tenantId(request.getTenantId()).name(request.getName())
                .tier(request.getTier()).status("ACTIVE")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).deleted(false)
                .build();
        tenantRepository.save(tenant);
        return Result.ok(TenantResponse.from(tenant));
    }

    @GetMapping
    @SaCheckPermission("tenant:read")
    @Operation(summary = "租户列表", description = "分页获取租户列表，需 tenant:read 权限")
    public Result<List<TenantResponse>> list(
            @Parameter(description = "页码（从 0 开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        log.debug("[Tenant] 查询租户列表: page={}, size={}", page, size);
        List<TenantResponse> list = tenantRepository.findAll(page, size).stream()
                .map(TenantResponse::from).toList();
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    @SaCheckPermission("tenant:read")
    @Operation(summary = "租户详情", description = "根据主键 ID 获取租户详情")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "租户不存在", content = @Content))
    public Result<TenantResponse> get(
            @Parameter(description = "租户主键 ID", example = "1") @PathVariable Long id) {
        log.debug("[Tenant] 查询租户详情: id={}", id);
        return tenantRepository.findById(id)
                .map(t -> Result.ok(TenantResponse.from(t)))
                .orElse(Result.notFound("租户不存在: " + id));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("tenant:write")
    @Operation(summary = "更新租户", description = "更新租户名称、套餐、配置，需 tenant:write 权限")
    public Result<TenantResponse> update(
            @Parameter(description = "租户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest request) {
        log.info("[Tenant] 更新租户: id={}", id);
        Tenant tenant = tenantRepository.findById(id).orElse(null);
        if (tenant == null) return Result.notFound("租户不存在: " + id);
        tenant = Tenant.builder()
                .id(tenant.getId()).tenantId(tenant.getTenantId())
                .name(request.getName()).tier(request.getTier() != null ? request.getTier() : tenant.getTier())
                .configJson(request.getConfigJson() != null ? request.getConfigJson() : tenant.getConfigJson())
                .status(tenant.getStatus()).createdAt(tenant.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(tenant.getDeleted())
                .build();
        tenantRepository.update(tenant);
        return Result.ok(TenantResponse.from(tenant));
    }

    @PatchMapping("/{id}/status")
    @SaCheckRole("TENANT_ADMIN")
    @Operation(summary = "启停租户", description = "修改租户状态（ACTIVE/SUSPENDED），需 TENANT_ADMIN 角色")
    public Result<TenantResponse> toggleStatus(
            @Parameter(description = "租户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTenantStatusRequest request) {
        log.info("[Tenant] 启停租户: id={}, status={}", id, request.getStatus());
        Tenant tenant = tenantRepository.findById(id).orElse(null);
        if (tenant == null) return Result.notFound("租户不存在: " + id);
        tenant = Tenant.builder()
                .id(tenant.getId()).tenantId(tenant.getTenantId()).name(tenant.getName())
                .tier(tenant.getTier()).status(request.getStatus())
                .configJson(tenant.getConfigJson()).createdAt(tenant.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(tenant.getDeleted())
                .build();
        tenantRepository.update(tenant);
        return Result.ok(TenantResponse.from(tenant));
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("TENANT_ADMIN")
    @Operation(summary = "删除租户（逻辑删除）", description = "逻辑删除租户，需 TENANT_ADMIN 角色")
    public Result<Void> delete(
            @Parameter(description = "租户主键 ID") @PathVariable Long id) {
        log.info("[Tenant] 删除租户: id={}", id);
        if (tenantRepository.findById(id).isEmpty()) return Result.notFound("租户不存在: " + id);
        tenantRepository.delete(id);
        return Result.ok();
    }
}
