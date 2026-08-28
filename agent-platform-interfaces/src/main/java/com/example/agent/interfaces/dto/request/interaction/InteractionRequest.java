package com.example.agent.interfaces.dto.request.interaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 统一交互请求 DTO — 模式无关的请求结构.
 * <p>
 * 前端在请求中指定 {@link #mode}，后端根据 mode 路由到对应的 {@code InteractionStrategy}。
 *
 * <pre>
 * CONVERSATION 模式必填: conversationId, content
 * KNOWLEDGE_SEARCH 模式必填: content, 可选 knowledgeId + searchConfig
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Data
@Schema(description = "统一交互请求")
public class InteractionRequest {

    @NotBlank(message = "交互模式不能为空")
    @Schema(description = "交互模式: CONVERSATION | KNOWLEDGE_SEARCH | TASK_EXECUTION | ANALYSIS", example = "CONVERSATION", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mode;

    @NotBlank(message = "输入内容不能为空")
    @Schema(description = "用户输入内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "会话 ID（CONVERSATION 模式必填）")
    private String conversationId;

    @Schema(description = "知识库 ID（KNOWLEDGE_SEARCH 模式可选，不填则检索所有启用的知识库）")
    private String knowledgeId;

    @Schema(description = "检索配置参数（KNOWLEDGE_SEARCH 模式可选）")
    private Map<String, Object> searchConfig;
}
