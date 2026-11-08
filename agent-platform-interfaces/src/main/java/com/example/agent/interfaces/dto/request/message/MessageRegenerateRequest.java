package com.example.agent.interfaces.dto.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 消息重生请求 — 基于已有助手消息对应的用户内容重新流式生成.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Data
@Schema(description = "消息重生请求")
public class MessageRegenerateRequest {

    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String conversationId;

    @NotBlank(message = "源消息ID不能为空")
    @Schema(description = "要重生的助手消息 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sourceMessageId;

    @Schema(description = "交互模式，默认 CONVERSATION", example = "CONVERSATION")
    private String mode;

    @Schema(description = "知识库 ID（KNOWLEDGE_SEARCH 可选）")
    private String knowledgeId;
}
