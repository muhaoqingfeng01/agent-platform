package com.example.agent.interfaces.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "启停用户请求（含ID）")
public class UserToggleStatusRequest {
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "目标状态")
    private String status;
}
