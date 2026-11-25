package com.example.agent.interfaces.dto.request.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 根据描述生成提示词正文请求（仅预览，不落库）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "根据描述生成提示词正文请求")
public class PromptGenerateRequest {

    @NotBlank(message = "描述不能为空")
    @Size(max = 2000, message = "描述最长2000个字符")
    @Schema(description = "模板用途描述", example = "客服场景下的系统人设，要求礼貌并优先引用来源")
    private String description;

    @Size(max = 256, message = "模板名称最长256个字符")
    @Schema(description = "可选模板名称，仅作生成上下文", example = "chat.system")
    private String name;
}
