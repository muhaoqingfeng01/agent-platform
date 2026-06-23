package com.example.agent.infrastructure.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.infrastructure.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户上下文拦截器
 * <p>
 * 在请求进入 Controller 前，从 Sa-Token Session 提取当前用户的
 * tenantId 和 userId，注入 {@link TenantContext} 和 SLF4J {@link MDC}。
 * <p>
 * MDC 注入责任分离：
 * <ul>
 *   <li><b>TraceFilter</b>：注入 traceId + spanId（请求到达时立即可用）</li>
 *   <li><b>TenantInterceptor</b>（此处）：注入 tenantId + userId（需 Sa-Token 认证完成后）</li>
 * </ul>
 * <p>
 * 注册方式：在 {@code SaTokenWebMvcConfig} 中
 * {@code registry.addInterceptor(tenantInterceptor)}。
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String MDC_TENANT_ID = "tenantId";
    private static final String MDC_USER_ID = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        try {
            // 尝试从 Sa-Token 获取登录用户信息
            if (StpUtil.isLogin()) {
                String userId = (String) StpUtil.getLoginId();
                Long tenantId = StpUtil.getSession().getLong("tenantId");

                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                    TenantContext.setUserId(userId);
                    // 注入 MDC（使后续日志自动携带租户/用户信息）
                    MDC.put(MDC_TENANT_ID, String.valueOf(tenantId));
                    MDC.put(MDC_USER_ID, userId);
                    log.trace("[TenantInterceptor] 上下文设置: tenantId={}, userId={}", tenantId, userId);
                } else {
                    // 🔴 已登录但 Session 中无 tenantId → 后续数据库写入会因 tenant_id NOT NULL 失败
                    log.warn("[TenantInterceptor] ⚠️ 已登录用户 {} 的 Session 中无 tenantId，" +
                             "后续涉及 tenant_id 的数据库写入将失败！", userId);
                    // 兜底：尝试从请求参数/Header 获取（某些场景下 tenantId 通过 Header 传递）
                    String headerTenantId = request.getHeader("X-Tenant-Id");
                    if (headerTenantId != null) {
                        try {
                            TenantContext.setTenantId(Long.parseLong(headerTenantId));
                            TenantContext.setUserId(userId);
                            log.info("[TenantInterceptor] 从 Header X-Tenant-Id 兜底获取 tenantId={}", headerTenantId);
                        } catch (NumberFormatException nfe) {
                            log.warn("[TenantInterceptor] Header X-Tenant-Id 解析失败: {}", headerTenantId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 未登录时静默跳过，由 Sa-Token 拦截器处理鉴权
            log.trace("[TenantInterceptor] 未登录用户，跳过租户上下文设置");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求完成后清理 ThreadLocal 和 MDC，防止内存泄漏
        TenantContext.clear();
        MDC.remove(MDC_TENANT_ID);
        MDC.remove(MDC_USER_ID);
        log.trace("[TenantInterceptor] ThreadLocal + MDC 已清理");
    }
}
