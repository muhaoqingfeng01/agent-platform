package com.example.agent.interfaces.dto.request.intent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 意图批量测试请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "意图批量测试请求")
public class IntentBatchTestRequest {

    @Schema(description = "测试项列表")
    private List<IntentBatchTestItem> items;
}
