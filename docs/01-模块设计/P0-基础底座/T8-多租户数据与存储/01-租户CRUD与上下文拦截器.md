# 租户 CRUD 与上下文拦截器

## 所属阶段
**P0 基础底座 → T8 多租户数据与存储**

## 使用技术
- Spring Web `HandlerInterceptor`
- `ThreadLocal`（TenantContext 上下文透传）
- MyBatis 拦截器（SQL 自动注入 tenant_id）

## 涉及数据库表
- `t_tenant`

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/tenants` | 创建租户 |
| GET | `/api/v1/tenants` | 租户列表（分页） |
| GET | `/api/v1/tenants/{id}` | 租户详情 |
| PUT | `/api/v1/tenants/{id}` | 更新租户 |
| PATCH | `/api/v1/tenants/{id}/status` | 启停租户（ACTIVE/SUSPENDED） |
| DELETE | `/api/v1/tenants/{id}` | 逻辑删除 |

## 实现方案

### 1. 租户上下文

```java
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    public static void setTenantId(String tenantId) { CURRENT_TENANT.set(tenantId); }
    public static String getCurrentTenantId() { return CURRENT_TENANT.get(); }
    public static void setUserId(String userId) { CURRENT_USER.set(userId); }
    public static String getCurrentUserId() { return CURRENT_USER.get(); }
    public static void clear() { CURRENT_TENANT.remove(); CURRENT_USER.remove(); }
}
```

### 2. 租户拦截器

```java
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从 Sa-Token Session 提取租户 ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String tenantId = JwtTokenProvider.getTenantIdFromToken(token.substring(7));
            String userId = JwtTokenProvider.getUserIdFromToken(token.substring(7));
            TenantContext.setTenantId(tenantId);
            TenantContext.setUserId(userId);
        }
        return true;
    }

    @Override
    public void afterCompletion(...) {
        TenantContext.clear();  // 防止内存泄漏
    }
}
```

### 3. SQL 自动注入（MyBatis 拦截器）

```java
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class TenantSqlInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        String originalSql = handler.getBoundSql().getSql();

        // 给 SELECT/UPDATE/DELETE 自动加 tenant_id 条件
        String tenantId = TenantContext.getCurrentTenantId();
        if (tenantId != null && needTenantFilter(originalSql)) {
            String newSql = injectTenantCondition(originalSql, tenantId);
            metaObject.setValue("delegate.boundSql.sql", newSql);
        }
        return invocation.proceed();
    }
}
```
