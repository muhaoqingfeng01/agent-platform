package com.example.agent.interfaces.dto.request.prompt;

import com.example.agent.domain.prompt.valueobject.VariableDef;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "编辑提示词请求（含ID）")
public class PromptUpdateRequest {
    @NotBlank(message = "模板ID不能为空")
    @Schema(description = "模板ID")
    private String id;
    @Schema(description = "模板名称")
    private String name;
    @Schema(description = "模板描述")
    private String description;
    @Schema(description = "模板文本")
    private String templateText;
    @Schema(description = "变量列表")
    private List<VariableDef> variables;
}
