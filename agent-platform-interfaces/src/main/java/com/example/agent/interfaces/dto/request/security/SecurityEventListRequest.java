package com.example.agent.interfaces.dto.request.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "安全事件列表请求")
public class SecurityEventListRequest {
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页大小", example = "20")
    private int size = 20;
}
