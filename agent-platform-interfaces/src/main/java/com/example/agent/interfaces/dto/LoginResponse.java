package com.example.agent.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 登录响应 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Sa-Token 令牌 */
    private String token;

    /** Token 类型（固定值 "Bearer"） */
    private String tokenType;

    /** 过期时间（秒） */
    private long expiresIn;

    public LoginResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = 3600; // 默认 1 小时
    }
}
