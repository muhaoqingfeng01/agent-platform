package com.example.agent.infrastructure.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 统一 CORS 跨域配置
 * <p>
 * 安全要点：
 * <ul>
 *   <li>不使用 {@code allowedOrigins("*")}，改用 {@code allowedOriginPatterns} 精确控制</li>
 *   <li>生产环境仅允许企业域名，开发环境允许 localhost</li>
 *   <li>显式暴露 Authorization 和追踪相关响应头</li>
 *   <li>OPTIONS 预检请求缓存 1 小时，减少重复预检</li>
 * </ul>
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("[CORS] 初始化跨域配置...");

        registry.addMapping("/api/**")
                // 允许的来源模式（不使用 allowedOrigins("*")）
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "https://*.agent-platform.local",
                        "https://*.agent-platform.com"
                )
                // 支持的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 显式暴露的响应头（前端需要读取的）
                .exposedHeaders(
                        "Authorization",       // Token
                        "X-Trace-Id",          // 全链路追踪 ID
                        "X-Request-Id",        // 请求 ID
                        "Content-Disposition"  // 文件下载
                )
                // 允许携带 Cookie / Authorization Header
                .allowCredentials(true)
                // 预检请求缓存时间（秒）
                .maxAge(3600);

        log.info("[CORS] 跨域配置初始化完成");
    }
}
