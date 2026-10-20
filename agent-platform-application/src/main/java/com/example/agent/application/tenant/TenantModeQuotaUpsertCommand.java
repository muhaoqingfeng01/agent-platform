package com.example.agent.application.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户模式配额 upsert 命令.
 */
@Data
@Schema(description = "保存租户模式配额")
public class TenantModeQuotaUpsertCommand {

    @Schema(description = "租户标识")
    private Long tenantId;

    @Schema(description = "模式编码")
    private String modeCode;

    @Schema(description = "是否开通")
    private Boolean enabled;

    @Schema(description = "每日 LLM 上限，null 表示不限制")
    private Integer dailyLlmLimit;

    @Schema(description = "每日上传字节上限")
    private Long dailyUploadBytes;
}
