package com.example.agent.interfaces.dto.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "加载更早消息请求")
public class MessageLoadBeforeRequest {
    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private String id;
    @NotBlank(message = "before游标不能为空")
    @Schema(description = "消息时间游标")
    private String before;
}
