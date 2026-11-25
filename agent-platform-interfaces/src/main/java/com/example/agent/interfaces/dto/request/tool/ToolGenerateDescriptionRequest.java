package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 根据草稿描述润色工具描述请求（仅预览，不落库）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "根据草稿描述润色工具描述请求")
public class ToolGenerateDescriptionRequest {

    @NotBlank(message = "描述不能为空")
    @Size(max = 2000, message = "描述最长2000个字符")
    @Schema(description = "用户草稿描述", example = "查订单，可以按时间筛")
    private String description;

    @Size(max = 128, message = "工具名称最长128个字符")
    @Schema(description = "可选工具名称，仅作生成上下文", example = "订单查询")
    private String name;

    @Size(max = 32, message = "工具类型最长32个字符")
    @Schema(description = "可选工具类型，仅作生成上下文", example = "HTTP",
            allowableValues = {"MCP", "HTTP", "BUILTIN", "CUSTOM"})
    private String toolType;
}
