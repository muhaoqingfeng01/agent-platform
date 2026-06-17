package com.example.agent.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 登录请求 DTO
 */
@Getter
@Setter
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 明文密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 租户标识（可选，多租户登录时指定） */
    private String tenantId;

    /** 认证提供者类型（可选，LOCAL/LDAP/SSO，默认 LOCAL） */
    private String provider;
}
