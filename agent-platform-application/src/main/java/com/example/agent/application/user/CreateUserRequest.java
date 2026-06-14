package com.example.agent.application.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    @Schema(description = "所属租户标识", example = "tenant_acme")
    private String tenantId;

    @NotBlank @Size(min = 3, max = 64)
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @NotBlank @Size(min = 8, max = 128)
    @Schema(description = "密码（至少 8 位）", example = "P@ssw0rd!")
    private String password;

    @Schema(description = "邮箱", example = "zhangsan@acme.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
