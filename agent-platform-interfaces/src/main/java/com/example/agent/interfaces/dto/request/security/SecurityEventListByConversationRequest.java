package com.example.agent.interfaces.dto.request.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "按会话查询安全事件请求")
public class SecurityEventListByConversationRequest {
    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private String conversationId;
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页大小", example = "20")
    private int size = 20;
}
