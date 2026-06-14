package com.example.agent.application.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleToUserRequest {
    @NotBlank
    @Schema(description = "用户唯一标识", example = "user_abc123")
    private String userId;
}
