package com.example.agent.interfaces.dto.request.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 预览渲染请求.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "预览渲染请求")
public class PreviewRenderRequest {

    @NotNull(message = "变量值不能为空")
    @Schema(description = "变量名 → 变量值的映射",
            example = "{\"role\": \"客服助手\", \"agent_name\": \"小智\", \"tone\": \"亲切友好\"}")
    private Map<String, Object> variables;
}
