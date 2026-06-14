# Sa-Token 认证方案

## 所属阶段
**P0 基础底座 → T2 统一网关与安全流控**

## 使用技术
- `cn.dev33:sa-token-spring-boot3-starter:1.39.0`
- `cn.dev33:sa-token-redis-jackson:1.39.0`
- Redis（Token 持久化 / 分布式会话 / 黑名单）

## 为什么选 Sa-Token 而非 JWT？

| 对比维度 | Sa-Token | JWT |
|----------|----------|-----|
| Token 风格 | 随机字符串（random-64） | 自包含载荷（header.payload.signature） |
| 踢人下线 | 原生支持，一行代码 | 需自建 Redis 黑名单 |
| Token 刷新 | 框架内置续期机制 | 需自行实现 RefreshToken |
| 权限注解 | `@SaCheckPermission` 开箱即用 | 需整合 Spring Security `@PreAuthorize` |
| 多端登录 | 框架原生多端隔离 | 需自行设计 |
| 微服务 | 深度集成 Redis，天然支持分布式 | 需自行处理跨服务校验 |
| 代码量 | ~200 行配置搞定 | ~500+ 行（Provider + Filter + Blacklist） |

## 实现方案

### 1. Maven 依赖

```xml
<!-- Sa-Token Spring Boot 3 集成 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot3-starter</artifactId>
    <version>1.39.0</version>
</dependency>
<!-- Sa-Token Redis 集成（Token 持久化 + 分布式） -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-redis-jackson</artifactId>
    <version>1.39.0</version>
</dependency>
```

### 2. 配置文件

```yaml
sa-token:
  token-name: Authorization      # Token 名称（从 Header 读取）
  token-prefix: Bearer           # Token 前缀
  timeout: 3600                  # Access Token 有效期（秒）
  active-timeout: 1800           # 临时有效期（持续活跃自动续期）
  is-concurrent: true            # 允许同一账号并发登录
  is-share: true                 # 多端共享同一 Token
  token-style: random-64         # 64位随机字符串
  is-log: true                   # 输出操作日志
  alone-redis:                   # Redis 独立配置
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 1                  # 使用 1 号库
```

### 3. Token 生命周期

```
登录 → StpUtil.login(userId) 
     → Sa-Token 自动生成 Token + 写入 Redis
     → 用户持续活跃 → Token 自动续期（每次请求重置 active-timeout）
     → 超过 active-timeout 未活跃 → Token 冻结（需重新登录）
     → 超过 timeout → Token 彻底过期

登出 → StpUtil.logout()     → 从 Redis 删除 Token
强制下线 → StpUtil.kickout(userId)  → 标记 Token 失效
```

### 4. 核心 API（替代 JwtTokenProvider）

| Sa-Token API | 说明 | 旧 JWT 对应 |
|-------------|------|------------|
| `StpUtil.login(userId)` | 登录，自动生成 Token | `generateAccessToken()` |
| `StpUtil.logout()` | 当前会话注销 | `TokenBlacklistService.blacklist()` |
| `StpUtil.isLogin()` | 校验是否登录 | `validateToken()` |
| `StpUtil.getLoginId()` | 获取当前用户 ID | `getAuthentication()` |
| `StpUtil.getTokenValue()` | 获取当前 Token 值 | — |
| `StpUtil.kickout(userId)` | 踢人下线 | 需自建黑名单 |
| `StpUtil.getSession()` | 获取当前 Session | — |
| `StpUtil.getPermissionList()` | 获取当前用户权限列表 | 查 JWT Claims |

### 5. 登录/登出示例

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. 校验账号密码
        User user = userService.authenticate(request.getUsername(), request.getPassword());
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        
        // 2. Sa-Token 登录（一行代码完成 Token 生成 + Redis 存储）
        StpUtil.login(user.getUserId());
        
        // 3. 将用户权限写入 Sa-Token Session（可选，也可在每次请求时查询）
        StpUtil.getSession().set("roles", user.getRoles());
        StpUtil.getSession().set("permissions", permissionService.getPermissions(user.getUserId()));
        StpUtil.getSession().set("tenantId", user.getTenantId());
        
        // 4. 返回 Token
        return Result.ok(new LoginResponse(StpUtil.getTokenValue()));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<UserInfo> currentUser() {
        // 从 Session 获取当前登录用户信息
        String userId = (String) StpUtil.getLoginId();
        String tenantId = StpUtil.getSession().getString("tenantId");
        return Result.ok(new UserInfo(userId, tenantId));
    }
}
```

### 6. Sa-Token 配置类

```java
@Configuration
public class SaTokenConfig {

    /**
     * 自定义权限校验规则：从数据库加载用户权限
     */
    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                // 从数据库查询用户权限列表
                return permissionService.getPermissionCodes((String) loginId);
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                // 从数据库查询用户角色列表
                return roleService.getRoleCodes((String) loginId);
            }
        };
    }

    /**
     * 自定义 Token 生成策略（可选）
     */
    @Bean
    public SaTokenContext saTokenContext() {
        return new SaTokenContext() {
            @Override
            public String getTenantId() {
                return StpUtil.getSession().getString("tenantId");
            }
        };
    }
}
```

### 7. 权限注解使用

```java
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    // Sa-Token 权限校验（替代 @PreAuthorize）
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
}
```

### 8. 安全要点

- Token 存储在 Redis 而非客户端载荷 → 服务端可控，天然支持踢人下线
- Redis 不可用时自动降级为内存存储（`alone-redis` 配置除外）
- 敏感操作（删除、审批）通过 `@SaCheckPermission` 双重校验
- Token 风格使用 `random-64`→ 不可逆、不可解析，安全性高于 JWT
