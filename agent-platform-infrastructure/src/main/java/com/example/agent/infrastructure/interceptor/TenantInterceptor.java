package com.example.agent.infrastructure.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.infrastructure.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户上下文拦截器
 * <p>
 * 在请求进入 Controller 前，从 Sa-Token Session 提取当前用户的
 * tenantId 和 userId，注入 {@link TenantContext}。
 * <p>
 * 注册方式：在 {@code SaTokenWebMvcConfig} 中
 * {@code registry.addInterceptor(tenantInterceptor)}。
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        try {
            // 尝试从 Sa-Token 获取登录用户信息
            if (StpUtil.isLogin()) {
                String userId = (String) StpUtil.getLoginId();
                String tenantId = StpUtil.getSession().getString("tenantId");

                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                    TenantContext.setUserId(userId);
                    log.trace("[TenantInterceptor] 上下文设置: tenantId={}, userId={}", tenantId, userId);
                } else {
                    log.trace("[TenantInterceptor] Session 中无 tenantId，跳过上下文设置");
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
        // 请求完成后清理 ThreadLocal，防止内存泄漏
        TenantContext.clear();
        log.trace("[TenantInterceptor] ThreadLocal 已清理");
    }
}
