package com.example.agent.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Token 刷新请求 DTO
 */
@Getter
@Setter
public class RefreshTokenRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Schema(description = "用户唯一标识", example = "user_abc12345")
    private String userId;

    @NotBlank
    @Schema(description = "刷新令牌（登录时获取）", example = "a1b2c3d4-e5f6-...")
    private String refreshToken;
}
