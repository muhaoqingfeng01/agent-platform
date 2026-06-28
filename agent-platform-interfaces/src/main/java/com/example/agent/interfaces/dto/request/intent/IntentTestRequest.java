package com.example.agent.interfaces.dto.request.intent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 意图测试请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "意图测试请求")
public class IntentTestRequest {

    @NotBlank(message = "意图ID不能为空")
    @Schema(description = "意图ID")
    private String id;

    @NotBlank(message = "输入不能为空")
    @Schema(description = "测试输入文本")
    private String input;
}
