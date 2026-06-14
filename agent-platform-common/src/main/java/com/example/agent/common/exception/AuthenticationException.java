package com.example.agent.common.exception;

/**
 * 认证异常（401）
 * <p>
 * 用户名/密码错误、Token 过期等认证失败场景。
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super(401, message);
    }
}
