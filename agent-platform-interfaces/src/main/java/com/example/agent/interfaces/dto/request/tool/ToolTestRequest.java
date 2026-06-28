package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 工具测试请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "工具测试请求")
public class ToolTestRequest {

    @NotBlank(message = "工具ID不能为空")
    @Schema(description = "工具ID")
    private String id;

    @Schema(description = "测试参数")
    private Map<String, Object> params;
}
