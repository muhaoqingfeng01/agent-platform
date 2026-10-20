package com.example.agent.application.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 租户模式配额列表响应 — 禁止 Controller 裸 List.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "租户模式配额列表")
public class TenantModeQuotaListResponse {

    @Schema(description = "租户标识")
    private Long tenantId;

    @Schema(description = "日切时区，固定 UTC")
    private String timezone;

    @Schema(description = "计数策略：SUCCESS_THEN_INCR 表示成功后再计入")
    private String countingPolicy;

    @Schema(description = "租户级上传限额（各模式 dailyUploadBytes 最小值），null 不限制")
    private Long uploadLimit;

    @Schema(description = "今日已用上传字节（UTC）")
    private Long uploadUsedToday;

    @Schema(description = "官方模式目录（含未落库的默认开通项）")
    private List<TenantModeQuotaItemResponse> items;
}
