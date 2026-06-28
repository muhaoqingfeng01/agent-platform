package com.example.agent.interfaces.dto.request.intent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 意图批量测试项 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "意图批量测试项")
public class IntentBatchTestItem {

    @Schema(description = "测试输入文本")
    private String input;

    @Schema(description = "期望意图编码")
    private String expectedIntentCode;
}
