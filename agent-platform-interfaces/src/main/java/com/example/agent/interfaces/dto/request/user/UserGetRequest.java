package com.example.agent.interfaces.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "用户ID请求")
public class UserGetRequest {
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long id;
}
