package com.example.agent.application.conversation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式取消响应.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "流式取消结果")
public class StreamCancelResponse {

    @Schema(description = "流 ID")
    private String streamId;

    @Schema(description = "会话 ID")
    private String conversationId;

    @Schema(description = "是否成功取消（false 表示流已结束或不存在）")
    private boolean cancelled;

    @Schema(description = "描述")
    private String message;
}
