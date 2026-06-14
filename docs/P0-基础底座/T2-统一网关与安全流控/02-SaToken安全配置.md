# Sa-Token 安全配置

## 所属阶段
**P0 基础底座 → T2 统一网关与安全流控**

## 使用技术
- Sa-Token 1.39.0（替代 Spring Security）
- Redis（Token 持久化与分布式会话）
- BCrypt 密码编码器（仍保留）

## 实现方案

### 1. 安全架构

```
请求 → SaTokenCorsFilter → SaTokenPathFilter(Sa-Token Auth)
     → Controller(@SaCheckPermission / @SaCheckRole)
```

> **注意**: Sa-Token 不需要 Spring Security 的 SecurityContext 体系。
> 鉴权信息通过 `StpUtil` 静态方法随时获取，无需 Filter 链传递。

### 2. Sa-Token 路由拦截配置

```java
@Configuration
public class SaTokenWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 指定需要校验的路由
                    SaRouter
                        // 白名单（无需登录）
                        .match("/api/v1/auth/login", "/api/v1/auth/register")
                        .match("/swagger-ui/**", "/v3/api-docs/**")
                        .match("/actuator/health", "/actuator/info")
                        .match("/ws/**")                    // WebSocket 握手
                        .stop()                             // 停止校验

                        // 管理员接口 → 需要 ADMIN 角色
                        .match("/api/v1/admin/**", r -> StpUtil.checkRole("ADMIN"))

                        // 其余接口 → 需要登录
                        .match("/api/v1/**", r -> StpUtil.checkLogin());
                }))
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/error");
    }
}
```

### 3. 权限注解使用

```java
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    @SaCheckPermission("tenant:write")
    @PostMapping
    public Result<Void> createTenant(@Valid @RequestBody CreateTenantRequest request) { ... }

    @SaCheckPermission("tenant:read")
    @GetMapping("/{id}")
    public Result<TenantResponse> getTenant(@PathVariable String id) { ... }

    // 角色校验
    @SaCheckRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTenant(@PathVariable String id) { ... }
    
    // 登录校验（最小要求）
    @SaCheckLogin
    @GetMapping("/profile")
    public Result<UserProfile> getProfile() { ... }
}
```

### 4. 自定义权限加载器

```java
@Component
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;
    private final PermissionService permissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // loginId = user.getUserId()
        return permissionService.getPermissionCodes((String) loginId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userService.getRoleCodes((String) loginId);
    }
}
```

### 5. 自定义租户校验器

```java
@Component("tenantValidator")
public class TenantPermissionValidator {

    public boolean isSameTenant(String tenantId) {
        return tenantId.equals(StpUtil.getSession().getString("tenantId"));
    }
}

// 使用（Sa-Token 支持 Spring SpEL 表达式）
@SaCheckPermission(value = "user:write", orRole = "ADMIN")
@PostMapping("/users")
public Result<Void> createUser(@RequestBody CreateUserRequest request) {
    if (!tenantValidator.isSameTenant(request.getTenantId())) {
        throw new SaTokenException("无权操作该租户数据");
    }
    // ...
}
```

### 6. 全局异常处理（与 Sa-Token 集成）

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Sa-Token 未登录异常
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleNotLogin(NotLoginException e) {
        return Result.fail(401, "未登录或 Token 已过期");
    }

    // Sa-Token 无权限异常
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotPermission(NotPermissionException e) {
        return Result.fail(403, "无操作权限: " + e.getPermission());
    }

    // Sa-Token 无角色异常
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotRole(NotRoleException e) {
        return Result.fail(403, "无角色权限: " + e.getRole());
    }
}
```

### 7. 密码加密（保留 BCrypt）

```java
@Component
public class PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

### 8. CORS 配置

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```
