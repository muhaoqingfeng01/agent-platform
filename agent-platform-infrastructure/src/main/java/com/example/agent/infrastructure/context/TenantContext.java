package com.example.agent.infrastructure.context;

/**
 * 租户上下文 — ThreadLocal 透传
 * <p>
 * 在请求进入时由 {@code TenantInterceptor} 设置，
 * 请求结束时自动清理，防止内存泄漏。
 * <p>
 * 使用示例：
 * <pre>{@code
 *   String tenantId = TenantContext.getCurrentTenantId();
 *   String userId = TenantContext.getCurrentUserId();
 * }</pre>
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private TenantContext() {
        // 工具类禁止实例化
    }

    /** 设置当前租户 ID */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /** 获取当前租户 ID */
    public static String getCurrentTenantId() {
        return CURRENT_TENANT.get();
    }

    /** 设置当前用户 ID */
    public static void setUserId(String userId) {
        CURRENT_USER.set(userId);
    }

    /** 获取当前用户 ID */
    public static String getCurrentUserId() {
        return CURRENT_USER.get();
    }

    /**
     * 清理 ThreadLocal
     * <p>
     * 必须在请求完成后调用，否则在线程池场景下会导致：
     * <ul>
     *   <li>内存泄漏（ThreadLocal 引用无法 GC）</li>
     *   <li>数据串扰（下一个请求读到上一个请求的租户 ID）</li>
     * </ul>
     */
    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_USER.remove();
    }
}
