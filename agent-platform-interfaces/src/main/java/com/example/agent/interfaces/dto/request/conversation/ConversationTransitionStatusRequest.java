package com.example.agent.interfaces.dto.request.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 会话状态流转请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "会话状态流转请求")
public class ConversationTransitionStatusRequest {
    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private String id;
    @NotBlank(message = "目标状态不能为空")
    @Schema(description = "目标状态")
    private String targetStatus;
}
