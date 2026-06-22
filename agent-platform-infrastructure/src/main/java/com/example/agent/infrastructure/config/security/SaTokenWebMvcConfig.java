package com.example.agent.infrastructure.config.security;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.infrastructure.interceptor.TenantInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Sa-Token Web MVC 路由拦截配置
 * <p>
 * 替代 Spring Security 的 Filter 链体系，通过拦截器实现：
 * <ul>
 *   <li>白名单路径放行（登录、注册、Swagger、健康检查、Prometheus）</li>
 *   <li>管理员接口角色校验</li>
 *   <li>其余 /api/v1/** 需要登录</li>
 * </ul>
 * <p>
 * 同时注册 {@link TenantInterceptor}，在 Sa-Token 认证完成后
 * 将 tenantId/userId 注入 {@code TenantContext} 和 SLF4J {@code MDC}。
 * <p>
 * 鉴权信息通过 {@code StpUtil} 静态方法随时获取，无需 Filter 链传递 SecurityContext。
 *
 * @see SaInterceptor
 * @see SaRouter
 * @see TenantInterceptor
 */
@Slf4j
@Configuration
public class SaTokenWebMvcConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    public SaTokenWebMvcConfig(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }

    /**
     * 注册 Sa-Token 路由拦截器
     * <p>
     * 拦截顺序：按 match 书写顺序依次匹配，命中后执行对应校验逻辑。
     * {@code .stop()} 表示停止后续匹配（白名单直接放行）。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("[Sa-Token] 初始化路由拦截规则...");

        // ==================== Sa-Token 认证拦截器 ====================
        registry.addInterceptor(new SaInterceptor(handler -> {

                    // ==================== 白名单（无需登录） ====================
                    SaRouter
                            .match("/api/v1/auth/login",
                                   "/api/v1/auth/refresh",
                                   "/api/v1/users/register",
                                   "/swagger-ui/**",
                                   "/v3/api-docs/**",
                                   "/doc.html",
                                   "/webjars/**",
                                   "/favicon.ico",
                                   "/actuator/health",
                                   "/actuator/info",
                                   "/actuator/prometheus",
                                   "/ws/**",
                                   "/error")
                            .stop();                                // 停止校验，直接放行

                    // ==================== 排除 OPTIONS 预检请求（CORS Preflight） ====================
                    // OPTIONS 请求不会携带 Token，必须放行以避免 CORS 失败
                    // 通过 Spring RequestContextHolder 获取当前请求
                    ServletRequestAttributes attributes = 
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        HttpServletRequest request = attributes.getRequest();
                        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                            return;  // 直接返回，不进行后续鉴权
                        }
                    }

                    // ==================== 管理员接口 → ADMIN 角色 ====================
                    SaRouter
                            .match("/api/v1/admin/**", r -> StpUtil.checkRole("ADMIN"));

                    // ==================== 其余 /api/v1/** → 需登录 ====================
                    SaRouter
                            .match("/api/v1/**", r -> StpUtil.checkLogin());

                }))
                .addPathPatterns("/**")                             // 拦截所有路径
                .excludePathPatterns("/static/**");                 // 静态资源排除

        // ==================== 租户上下文拦截器（认证后注入 tenantId/userId 到 MDC） ====================
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**");

        log.info("[Sa-Token] 路由拦截规则 + TenantInterceptor 初始化完成");
    }
}
