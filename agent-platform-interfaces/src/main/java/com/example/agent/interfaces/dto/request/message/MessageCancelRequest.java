package com.example.agent.interfaces.dto.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 取消流式输出请求.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Data
@Schema(description = "取消流式输出请求")
public class MessageCancelRequest {

    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String conversationId;

    @NotBlank(message = "流ID不能为空")
    @Schema(description = "流ID（SSE stream_started 事件下发）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String streamId;
}
