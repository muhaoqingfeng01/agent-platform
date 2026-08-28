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
    @NotBlank(message = "反馈类型不能为空")
    @Schema(description = "反馈类型: LIKE / DISLIKE / NONE（取消）")
    private String feedback;

    @Schema(description = "点踩原因: 不准确 / 不完整 / 不安全 / 其他（点踩时建议填写）")
    private String reason;
}
