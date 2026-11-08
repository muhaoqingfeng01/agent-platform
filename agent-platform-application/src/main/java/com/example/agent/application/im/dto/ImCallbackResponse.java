package com.example.agent.application.im.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IM 回调处理结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "IM 回调结果")
public class ImCallbackResponse {

    @Schema(description = "是否已受理")
    private boolean accepted;

    @Schema(description = "内部会话 ID")
    private String conversationId;

    @Schema(description = "回复预览")
    private String reply;
}
