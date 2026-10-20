package com.example.agent.application.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单条模式配额响应.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "租户模式配额项")
public class TenantModeQuotaItemResponse {

    @Schema(description = "模式编码")
    private String modeCode;

    @Schema(description = "模式名称")
    private String modeDesc;

    @Schema(description = "是否开通")
    private Boolean enabled;

    @Schema(description = "每日 LLM 上限，null 表示不限制")
    private Integer dailyLlmLimit;

    @Schema(description = "该模式行上的每日上传字节上限")
    private Long dailyUploadBytes;

    @Schema(description = "今日已用 LLM 次数（UTC）")
    private Long llmUsedToday;
}
