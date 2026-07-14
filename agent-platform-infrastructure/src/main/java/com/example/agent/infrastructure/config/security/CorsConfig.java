package com.example.agent.infrastructure.config.security;

import com.example.agent.infrastructure.config.nacos.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 统一 CORS 跨域配置.
 * <p>🆕 P6 配置治理子方案03: 域名列表和 maxAge 从 SecurityConfig（Nacos 动态配置）读取.
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final SecurityConfig securityConfig;

    public CorsConfig(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("[CORS] 初始化跨域配置...");

        registry.addMapping("/api/**")
                .allowedOriginPatterns(securityConfig.getCorsAllowedOrigins().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders(
                        "Authorization",
                        "X-Trace-Id",
                        "X-Request-Id",
                        "Content-Disposition"
                )
                .allowCredentials(true)
                .maxAge(securityConfig.getCorsMaxAgeSeconds());

        log.info("[CORS] 跨域配置初始化完成");
    }
}
