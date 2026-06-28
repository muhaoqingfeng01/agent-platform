package com.example.agent.interfaces.dto.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "消息反馈请求（含路径参数）")
public class MessageFeedbackRequest {
    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private String conversationId;
    @NotBlank(message = "消息ID不能为空")
    @Schema(description = "消息ID")
    private String msgId;
    @Schema(description = "反馈类型")
    private String feedback;
}
