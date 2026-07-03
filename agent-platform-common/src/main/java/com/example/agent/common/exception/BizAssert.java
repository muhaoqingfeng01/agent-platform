package com.example.agent.common.exception;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 业务断言工具 — 替代应用层散落的 {@code if (xxx == null) throw new BusinessException(...)} 模板.
 * <p>
 * 对标 {@code org.springframework.util.Assert}，断言失败时抛出 {@link BusinessException}（携带业务状态码），
 * 由 {@code GlobalExceptionHandler} 统一处理返回前端。
 * <p>
 * 消息建议使用 {@link ExceptionMessages} 中的常量，避免硬编码字符串。
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 空值守卫
 * BizAssert.notNull(user, 404, ExceptionMessages.USER_NOT_FOUND + userId);
 *
 * // 状态守卫 — 判断是否到达预期值
 * BizAssert.isReached(DocumentStatus.PARSED, doc.getStatus(), 400,
 *         "文档必须先解析完成才能切片");
 *
 * // 业务条件守卫
 * BizAssert.isTrue(balance >= amount, 400, "余额不足");
 *
 * // 空字符串守卫
 * BizAssert.hasText(name, 400, ExceptionMessages.VALIDATION_ERROR + "名称不能为空");
 *
 * // 空集合守卫
 * BizAssert.notEmpty(results, 404, "查询结果为空");
 *
 * // 通用状态断言
 * BizAssert.state(order.isActive(), 400, "订单已失效，无法操作");
 * }</pre>
 *
 * @author Agent Platform Team
 * @see BusinessException
 * @see ExceptionMessages
 * @since 1.7.0
 */
public abstract class BizAssert {

    private BizAssert() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    // ==================== 含 code 参数（核心 API） ====================

    /**
     * 断言对象不为 null.
     *
     * @param object  待检查对象
     * @param code    业务状态码（失败时透传）
     * @param message 失败时的异常消息
     * @throws BusinessException 当 object 为 null
     */
    public static void notNull(@Nullable Object object, int code, String message) {
        if (object == null) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言条件为 true.
     *
     * @param expression 待检查布尔条件
     * @param code       业务状态码（失败时透传）
     * @param message    失败时的异常消息
     * @throws BusinessException 当 expression 为 false
     */
    public static void isTrue(boolean expression, int code, String message) {
        if (!expression) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言字符串非空且非空白.
     * <p>
     * 使用 {@link String#isBlank()} 判断，会同时拒绝纯空白字符（空格、制表符等）。
     *
     * @param text    待检查字符串
     * @param code    业务状态码（失败时透传）
     * @param message 失败时的异常消息
     * @throws BusinessException 当 text 为 null、空串或纯空白
     */
    public static void hasText(@Nullable String text, int code, String message) {
        if (text == null || text.isBlank()) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言集合非空（不为 null 且至少有一个元素）.
     *
     * @param collection 待检查集合
     * @param code       业务状态码（失败时透传）
     * @param message    失败时的异常消息
     * @throws BusinessException 当 collection 为 null 或无元素
     */
    public static void notEmpty(@Nullable Collection<?> collection, int code, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言 Map 非空（不为 null 且至少有一个 entry）.
     *
     * @param map     待检查 Map
     * @param code    业务状态码（失败时透传）
     * @param message 失败时的异常消息
     * @throws BusinessException 当 map 为 null 或无 entry
     */
    public static void notEmpty(@Nullable Map<?, ?> map, int code, String message) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言实际值已达到期望值（使用 {@link Objects#equals} 比较）.
     * <p>
     * 典型场景：状态机中确认前序状态已完成、计数器达到阈值等。
     *
     * <pre>{@code
     * BizAssert.isReached(DocumentStatus.PARSED, doc.getStatus(), 400,
     *         "文档必须先解析完成才能切片");
     * }</pre>
     *
     * @param expected 期望值（目标状态/值）
     * @param actual   实际值
     * @param code     业务状态码（失败时透传）
     * @param message  失败时的异常消息
     * @throws BusinessException 当 expected 与 actual 不相等
     */
    public static void isReached(Object expected, Object actual, int code, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 通用状态断言 — 语义上等价于 {@link #isTrue(boolean, int, String)}，
     * 但命名上更适用于领域状态校验场景.
     *
     * @param expression 待检查状态条件
     * @param code       业务状态码（失败时透传）
     * @param message    失败时的异常消息
     * @throws BusinessException 当 expression 为 false
     */
    public static void state(boolean expression, int code, String message) {
        if (!expression) {
            throw new BusinessException(code, message);
        }
    }

    // ==================== 重载：默认 code=500 ====================

    /**
     * 断言对象不为 null（默认 code=500）.
     *
     * @param object  待检查对象
     * @param message 失败时的异常消息
     * @throws BusinessException(code=500) 当 object 为 null
     */
    public static void notNull(@Nullable Object object, String message) {
        notNull(object, 500, message);
    }

    /**
     * 断言条件为 true（默认 code=500）.
     *
     * @param expression 待检查布尔条件
     * @param message    失败时的异常消息
     * @throws BusinessException(code=500) 当 expression 为 false
     */
    public static void isTrue(boolean expression, String message) {
        isTrue(expression, 500, message);
    }

    /**
     * 断言字符串非空且非空白（默认 code=500）.
     *
     * @param text    待检查字符串
     * @param message 失败时的异常消息
     * @throws BusinessException(code=500) 当 text 为 null、空串或纯空白
     */
    public static void hasText(@Nullable String text, String message) {
        hasText(text, 500, message);
    }

    /**
     * 断言集合非空（默认 code=500）.
     *
     * @param collection 待检查集合
     * @param message    失败时的异常消息
     * @throws BusinessException(code=500) 当 collection 为 null 或无元素
     */
    public static void notEmpty(@Nullable Collection<?> collection, String message) {
        notEmpty(collection, 500, message);
    }

    /**
     * 断言 Map 非空（默认 code=500）.
     *
     * @param map     待检查 Map
     * @param message 失败时的异常消息
     * @throws BusinessException(code=500) 当 map 为 null 或无 entry
     */
    public static void notEmpty(@Nullable Map<?, ?> map, String message) {
        notEmpty(map, 500, message);
    }

    /**
     * 断言实际值已达到期望值（默认 code=500）.
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  失败时的异常消息
     * @throws BusinessException(code=500) 当 expected 与 actual 不相等
     */
    public static void isReached(Object expected, Object actual, String message) {
        isReached(expected, actual, 500, message);
    }

    /**
     * 通用状态断言（默认 code=500）.
     *
     * @param expression 待检查状态条件
     * @param message    失败时的异常消息
     * @throws BusinessException(code=500) 当 expression 为 false
     */
    public static void state(boolean expression, String message) {
        state(expression, 500, message);
    }
}
