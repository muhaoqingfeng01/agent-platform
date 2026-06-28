package com.example.agent.interfaces.dto.request.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "敏感词ID请求")
public class SensitiveWordGetRequest {
    @NotNull(message = "敏感词ID不能为空")
    @Schema(description = "敏感词ID")
    private Long id;
}
