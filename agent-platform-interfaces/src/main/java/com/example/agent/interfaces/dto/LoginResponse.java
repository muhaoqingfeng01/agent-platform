package com.example.agent.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 登录响应 DTO — 含用户上下文，避免前端登录后再调一次 /me 才能刷新 Token。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Sa-Token 令牌 */
    private String token;

    /** 刷新令牌（用于获取新的 Access Token） */
    private String refreshToken;

    /** Token 类型（固定值 "Bearer"） */
    private String tokenType;

    /** 过期时间（秒） */
    private long expiresIn;

    /** 用户唯一标识（刷新 Token 时必填） */
    private String userId;

    /** 用户名 */
    private String username;

    /** 所属租户 */
    private Long tenantId;

    /** 角色编码列表 */
    private List<String> roles;

    /** 权限码列表（如 kb:read） */
    private List<String> permissions;

    public LoginResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = 3600;
    }

    public LoginResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = 3600;
    }
}
