package com.example.agent.interfaces.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "修改密码请求（含ID）")
public class UserChangePasswordRequest {
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "旧密码")
    private String oldPassword;
    @Schema(description = "新密码")
    private String newPassword;
}
