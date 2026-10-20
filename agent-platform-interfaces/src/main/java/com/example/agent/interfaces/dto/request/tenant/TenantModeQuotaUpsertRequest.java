package com.example.agent.interfaces.dto.request.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "保存租户模式配额")
public class TenantModeQuotaUpsertRequest {

    @NotNull(message = "租户标识不能为空")
    @Schema(description = "租户唯一标识 tenantId")
    private Long tenantId;

    @NotBlank(message = "模式编码不能为空")
    @Schema(description = "交互模式编码")
    private String modeCode;

    @NotNull(message = "enabled 不能为空")
    @Schema(description = "是否开通该模式")
    private Boolean enabled;

    @Min(value = 0, message = "每日 LLM 上限不能为负数")
    @Schema(description = "每日 LLM 上限，null 表示不限制")
    private Integer dailyLlmLimit;

    @Min(value = 0, message = "每日上传字节上限不能为负数")
    @Schema(description = "每日上传字节上限，null 表示不限制")
    private Long dailyUploadBytes;
}
