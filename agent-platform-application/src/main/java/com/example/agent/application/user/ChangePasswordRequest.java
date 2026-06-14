package com.example.agent.application.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank
    @Schema(description = "旧密码")
    private String oldPassword;

    @NotBlank @Size(min = 8, max = 128)
    @Schema(description = "新密码（至少 8 位）")
    private String newPassword;
}
