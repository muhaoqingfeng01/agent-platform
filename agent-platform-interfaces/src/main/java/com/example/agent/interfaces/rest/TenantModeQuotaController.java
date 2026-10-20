package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.tenant.TenantModeQuotaApplicationService;
import com.example.agent.application.tenant.TenantModeQuotaItemResponse;
import com.example.agent.application.tenant.TenantModeQuotaListResponse;
import com.example.agent.application.tenant.TenantModeQuotaUpsertCommand;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.tenant.TenantModeQuotaListRequest;
import com.example.agent.interfaces.dto.request.tenant.TenantModeQuotaUpsertRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 租户模式市场与配额管理 — 一期仅官方模式开关 + 日限额 CRUD.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@RestController
@RequestMapping("/api/v1/tenants/mode-quotas")
@RequiredArgsConstructor
@Tag(name = "租户模式配额", description = "官方交互模式开关与日配额")
public class TenantModeQuotaController {

    private final TenantModeQuotaApplicationService quotaService;

    @PostMapping("/list")
    @SaCheckPermission("tenant:read")
    @Operation(summary = "查询租户模式配额（官方目录 + 已用计数）")
    public Result<TenantModeQuotaListResponse> list(@Valid @RequestBody TenantModeQuotaListRequest request) {
        return ResultRespHelper.responseInvoke("TenantModeQuotaController.list", request, (req) ->
                quotaService.list(req.getTenantId()));
    }

    @PostMapping("/upsert")
    @SaCheckPermission("tenant:write")
    @Operation(summary = "保存租户某一模式的开关与限额")
    public Result<TenantModeQuotaItemResponse> upsert(@Valid @RequestBody TenantModeQuotaUpsertRequest request) {
        return ResultRespHelper.responseInvoke("TenantModeQuotaController.upsert", request, (req) -> {
            TenantModeQuotaUpsertCommand command = new TenantModeQuotaUpsertCommand();
            command.setTenantId(req.getTenantId());
            command.setModeCode(req.getModeCode());
            command.setEnabled(req.getEnabled());
            command.setDailyLlmLimit(req.getDailyLlmLimit());
            command.setDailyUploadBytes(req.getDailyUploadBytes());
            return quotaService.upsert(command);
        });
    }
}
