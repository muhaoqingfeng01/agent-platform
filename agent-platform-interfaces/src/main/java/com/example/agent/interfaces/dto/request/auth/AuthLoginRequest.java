package com.example.agent.interfaces.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 认证登录请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "认证登录请求")
public class AuthLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "明文密码")
    private String password;

    @Schema(description = "租户标识（可选，多租户登录时指定）")
    private Long tenantId;

    @Schema(description = "认证提供者类型（可选，LOCAL/LDAP/SSO，默认 LOCAL）")
    private String provider;
}
