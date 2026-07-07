package com.example.agent.interfaces.dto.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 消息发送请求 DTO — 支持多模式交互.
 * <p>
 * 通过 {@link #mode} 字段指定交互模式（默认 CONVERSATION），
 * 后端根据模式路由到对应的执行管线。
 *
 * <pre>
 * CONVERSATION 模式必填: conversationId, content
 * KNOWLEDGE_SEARCH 模式必填: conversationId, content, 可选 knowledgeId
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "消息发送请求")
public class MessageSendRequest {

    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String conversationId;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "交互模式: CONVERSATION(智能对话，默认) | KNOWLEDGE_SEARCH(知识库检索)",
            example = "CONVERSATION", defaultValue = "CONVERSATION")
    private String mode;

    @Schema(description = "知识库 ID（KNOWLEDGE_SEARCH 模式可选，不填则检索当前租户下所有已启用的知识库）")
    private String knowledgeId;
}
