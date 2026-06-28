package com.example.agent.interfaces.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Token 刷新请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "Token刷新请求")
public class AuthRefreshTokenRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户唯一标识", example = "user_abc12345")
    private String userId;

    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌（登录时获取）", example = "a1b2c3d4-e5f6-...")
    private String refreshToken;
}
