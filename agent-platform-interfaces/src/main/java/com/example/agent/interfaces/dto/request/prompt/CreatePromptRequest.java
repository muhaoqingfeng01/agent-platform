package com.example.agent.interfaces.dto.request.prompt;

import com.example.agent.domain.prompt.valueobject.VariableDef;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建提示词模板请求.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "创建提示词模板请求")
public class CreatePromptRequest {

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 256, message = "模板名称最长256个字符")
    @Schema(description = "模板名称", example = "客服系统提示词")
    private String name;

    @Schema(description = "模板描述", example = "用于客服对话的 System Prompt")
    private String description;

    @NotBlank(message = "模板文本不能为空")
    @Schema(description = "模板文本（支持 {{variable}} 占位符）",
            example = "你是一个专业的{{role}}，名叫{{agent_name}}。\n请以{{tone}}的语气回答。")
    private String templateText;

    @Schema(description = "变量定义列表")
    private List<VariableDef> variables;

    @Schema(description = "A/B 测试配置 JSON")
    private String abTestConfig;
}
