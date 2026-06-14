# 会话 03 — Swagger 集成

**日期**: 2026-06-11
**触发**: 项目需要集成 Swagger 生成接口文档

---

## 执行的操作

### 1. 创建 OpenApiConfig

**文件**: `agent-platform-interfaces/src/main/java/com/example/agent/interfaces/config/OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI agentPlatformOpenAPI() { ... }
}
```

**配置内容**:
- API 标题: "Agent Platform API"
- 版本: 1.0.0
- Sa-Token Bearer 鉴权（全局 SecurityScheme）
- 3 个服务器环境（local/dev/prod）
- 12 个 API 分组标签（对应各阶段的 Controller）

### 2. 添加 YAML 配置

**文件**: `application.yml`

```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: method
    try-it-out-enabled: true
```

### 3. 遇到的问题

**编译错误**: 最初将 OpenApiConfig 放在 `infrastructure` 模块，但该模块没有 `springdoc-openapi` 依赖。

**解决方案**: 移动到 `interfaces` 模块（`com.example.agent.interfaces.config`），因为 `springdoc-openapi-starter-webmvc-ui` 依赖在 `interfaces/pom.xml` 中。

---

## 最终结果

- ✅ 编译通过
- 访问地址: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- 鉴权: 右上角 Authorize 按钮输入 Sa-Token
