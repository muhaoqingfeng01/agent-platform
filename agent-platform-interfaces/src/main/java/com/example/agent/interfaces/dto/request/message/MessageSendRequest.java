package com.example.agent.interfaces.dto.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 消息发送请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "消息发送请求")
public class MessageSendRequest {

    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private String conversationId;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;
}
