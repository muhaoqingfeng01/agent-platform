package com.example.agent.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 统一 API 响应结果封装
 * <p>
 * 所有 Controller 返回值统一使用此类包装，
 * 前端根据 code 判断业务状态，message 获取提示信息。
 *
 * @param <T> 响应数据类型
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功状态码 */
    public static final int SUCCESS_CODE = 200;
    /** 通用失败状态码 */
    public static final int FAIL_CODE = 500;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 时间戳 */
    private long timestamp;

    /** 等待指令，要求客户端等待几秒后再执行 */
    private int await;

    /** 执行时间（毫秒） */
    private int executionTime;

    // ==================== 私有构造器 ====================

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功静态工厂方法 ====================

    /** 成功（无数据） */
    public static <T> Result<T> ok() {
        return new Result<>(SUCCESS_CODE, "操作成功", null);
    }

    /** 成功（带数据） */
    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS_CODE, "操作成功", data);
    }

    /** 成功（自定义消息 + 数据） */
    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    // ==================== 失败静态工厂方法 ====================

    /** 失败（默认消息） */
    public static <T> Result<T> fail() {
        return new Result<>(FAIL_CODE, "操作失败", null);
    }

    /** 失败（自定义消息） */
    public static <T> Result<T> fail(String message) {
        return new Result<>(FAIL_CODE, message, null);
    }

    /** 失败（自定义状态码 + 消息） */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /** 失败（自定义状态码 + 消息 + 数据） */
    public static <T> Result<T> fail(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    // ==================== 常用 HTTP 错误快捷方法 ====================

    /** 400 Bad Request */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }

    /** 401 Unauthorized */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未认证，请先登录", null);
    }

    /** 401 Unauthorized（自定义消息） */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    /** 403 Forbidden */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "无权限访问", null);
    }

    /** 403 Forbidden（自定义消息） */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null);
    }

    /** 404 Not Found */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }

    /** 409 Conflict */
    public static <T> Result<T> conflict(String message) {
        return new Result<>(409, message, null);
    }

    /** 429 Too Many Requests */
    public static <T> Result<T> tooManyRequests(String message) {
        return new Result<>(429, message, null);
    }

    /** 500 Internal Server Error */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // ==================== 便捷判断 ====================

    /** 是否成功 */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }
}
