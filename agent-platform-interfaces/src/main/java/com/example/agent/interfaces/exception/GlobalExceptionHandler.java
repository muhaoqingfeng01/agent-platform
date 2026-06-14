package com.example.agent.interfaces.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.DuplicateResourceException;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.exception.TenantNotActiveException;
import com.example.agent.common.exception.AuthenticationException;
import com.example.agent.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 统一处理各类异常，返回 {@link Result} 格式的响应。
 * 涵盖 Sa-Token 鉴权异常、参数校验异常、业务异常等。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Sa-Token 鉴权异常 ====================

    /**
     * Sa-Token 未登录异常
     * <p>
     * 触发场景：未携带 Token 或 Token 已过期访问需要登录的接口。
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleNotLogin(NotLoginException e) {
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供有效 Token，请先登录";
            case NotLoginException.INVALID_TOKEN -> "Token 无效或已过期";
            case NotLoginException.TOKEN_TIMEOUT -> "Token 已过期，请重新登录";
            case NotLoginException.BE_REPLACED -> "账号在其他设备登录，当前 Token 已失效";
            case NotLoginException.KICK_OUT -> "账号已被管理员强制下线";
            default -> "未登录";
        };
        log.warn("[Exception] 未登录: type={}, message={}", e.getType(), message);
        return Result.fail(401, message);
    }

    /**
     * Sa-Token 无权限异常
     * <p>
     * 触发场景：@SaCheckPermission 校验失败。
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotPermission(NotPermissionException e) {
        log.warn("[Exception] 无操作权限: permission={}, userId={}",
                e.getPermission(),
                tryGetLoginId());
        return Result.fail(403, "无操作权限: " + e.getPermission());
    }

    /**
     * Sa-Token 无角色异常
     * <p>
     * 触发场景：@SaCheckRole 校验失败。
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotRole(NotRoleException e) {
        log.warn("[Exception] 无角色权限: role={}, userId={}",
                e.getRole(),
                tryGetLoginId());
        return Result.fail(403, "无角色权限，需要: " + e.getRole());
    }

    // ==================== 参数校验异常 ====================

    /**
     * Jakarta Bean Validation 校验失败（@Valid 触发的校验）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("[Exception] 参数校验失败: {}", message);
        return Result.fail(400, message);
    }

    /**
     * 单字段约束校验失败（@NotBlank 等在方法参数上的校验）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("[Exception] 参数约束失败: {}", e.getMessage());
        return Result.fail(400, "参数错误: " + e.getMessage());
    }

    // ==================== 限流熔断异常 ====================

    /**
     * Sentinel 限流/熔断异常
     * <p>
     * 触发场景：被 Sentinel 流控规则或熔断规则拦截时。
     */
    @ExceptionHandler(BlockException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Result<Void> handleBlock(BlockException e) {
        log.warn("[Exception] Sentinel 限流/熔断: rule={}, resource={}",
                e.getRule().getClass().getSimpleName(), e.getRule().getResource());
        return Result.tooManyRequests("请求过于频繁，请稍后再试");
    }

    // ==================== 业务异常 ====================

    /**
     * 通用业务异常（根据 code 返回对应 HTTP 状态码）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        log.warn("[Exception] 业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 资源不存在异常（404）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNotFound(ResourceNotFoundException e) {
        log.warn("[Exception] 资源不存在: {}", e.getMessage());
        return Result.notFound(e.getMessage());
    }

    /**
     * 重复资源异常（409）
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleDuplicate(DuplicateResourceException e) {
        log.warn("[Exception] 资源重复: {}", e.getMessage());
        return Result.conflict(e.getMessage());
    }

    /**
     * 租户已停用异常（403）
     */
    @ExceptionHandler(TenantNotActiveException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleTenantNotActive(TenantNotActiveException e) {
        log.warn("[Exception] 租户已停用: {}", e.getMessage());
        return Result.forbidden(e.getMessage());
    }

    /**
     * 认证异常（401）
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthentication(AuthenticationException e) {
        log.warn("[Exception] 认证失败: {}", e.getMessage());
        return Result.unauthorized(e.getMessage());
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[Exception] 非法参数: {}", e.getMessage());
        return Result.fail(400, e.getMessage());
    }

    // ==================== 兜底异常 ====================

    /**
     * 未预期的运行时异常（兜底处理）
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntime(RuntimeException e) {
        log.error("[Exception] 未预期的运行时异常", e);
        return Result.fail(500, "服务器内部错误，请联系管理员");
    }

    /**
     * 所有未捕获的异常（终极兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("[Exception] 未知异常", e);
        return Result.fail(500, "服务器内部错误");
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 安全获取当前登录用户 ID（鉴权失败时调用，可能为 null）
     */
    private String tryGetLoginId() {
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            return loginId != null ? loginId.toString() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }
}
