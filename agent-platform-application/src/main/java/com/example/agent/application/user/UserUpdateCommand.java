package com.example.agent.application.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserUpdateCommand {
    private Long id;
    @Schema(description = "邮箱", example = "newemail@acme.com")
    private String email;

    @Schema(description = "手机号", example = "13900139000")
    private String phone;
}
