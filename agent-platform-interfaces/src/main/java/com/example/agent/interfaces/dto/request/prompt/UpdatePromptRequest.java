package com.example.agent.interfaces.dto.request.prompt;

import com.example.agent.domain.prompt.valueobject.VariableDef;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 编辑提示词模板请求.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "编辑提示词模板请求")
public class UpdatePromptRequest {

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "模板文本")
    private String templateText;

    @Schema(description = "变量定义列表")
    private List<VariableDef> variables;
}
