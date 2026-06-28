package com.example.agent.interfaces.dto.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "消息列表查询请求")
public class MessageListRequest {
    @Schema(description = "会话ID")
    private String id;
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页大小", example = "50")
    private int size = 50;
}
