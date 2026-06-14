package com.example.agent.common.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * <p>
 * 所有业务异常继承此类，由 {@code GlobalExceptionHandler} 统一处理。
 * 携带业务状态码，直接透传给前端。
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务状态码（HTTP 状态码或自定义业务码） */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
}
