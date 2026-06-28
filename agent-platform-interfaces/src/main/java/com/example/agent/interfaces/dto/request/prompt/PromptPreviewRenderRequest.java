package com.example.agent.interfaces.dto.request.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 提示词预览渲染请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "提示词预览渲染请求")
public class PromptPreviewRenderRequest {

    @NotBlank(message = "模板ID不能为空")
    @Schema(description = "模板ID")
    private String id;

    @Schema(description = "变量值列表")
    private Map<String, Object> variables;
}
