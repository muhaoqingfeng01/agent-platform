package com.example.agent.infrastructure.config.security;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token Web MVC 路由拦截配置
 * <p>
 * 替代 Spring Security 的 Filter 链体系，通过拦截器实现：
 * <ul>
 *   <li>白名单路径放行（登录、注册、Swagger、健康检查）</li>
 *   <li>管理员接口角色校验</li>
 *   <li>其余 /api/v1/** 需要登录</li>
 * </ul>
 * <p>
 * 鉴权信息通过 {@code StpUtil} 静态方法随时获取，无需 Filter 链传递 SecurityContext。
 *
 * @see SaInterceptor
 * @see SaRouter
 */
@Slf4j
@Configuration
public class SaTokenWebMvcConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 路由拦截器
     * <p>
     * 拦截顺序：按 match 书写顺序依次匹配，命中后执行对应校验逻辑。
     * {@code .stop()} 表示停止后续匹配（白名单直接放行）。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("[Sa-Token] 初始化路由拦截规则...");

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
                                   "/ws/**",
                                   "/error")
                            .stop();                                // 停止校验，直接放行

                    // ==================== 管理员接口 → ADMIN 角色 ====================
                    SaRouter
                            .match("/api/v1/admin/**", r -> StpUtil.checkRole("ADMIN"));

                    // ==================== 其余 /api/v1/** → 需登录 ====================
                    SaRouter
                            .match("/api/v1/**", r -> StpUtil.checkLogin());

                }))
                .addPathPatterns("/**")                             // 拦截所有路径
                .excludePathPatterns("/static/**");                 // 静态资源排除

        log.info("[Sa-Token] 路由拦截规则初始化完成");
    }
}
