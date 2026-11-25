# 统一 CORS 跨域配置

## 所属阶段
**P0 基础底座 → T2 统一网关与安全流控**

## 使用技术
- Spring Web MVC `CorsConfiguration`
- Sa-Token CORS 集成

## 实现方案

### 1. CORS 配置

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "https://*.agent-platform.local")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "X-Trace-Id", "X-Request-Id")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### 2. 安全要点

- **不使用** `allowedOrigins("*")`，改用 `allowedOriginPatterns` 精确控制
- 生产环境仅允许企业域名
- WebSocket 端点单独处理 CORS（通过 `setAllowedOrigins`）
- OPTIONS 预检请求白名单放行
