# P0 企业认证与 Sentinel 限流面板

## 所属阶段
**P0 基础底座 → T2 统一网关与安全流控**

## 使用技术
- LDAP / OAuth2.0 / OIDC（企业统一认证）
- Sentinel Dashboard 1.8.x（限流规则可视化控制台）
- Spring Security OAuth2 Client（可选，替代 Sa-Token 部分职责）

---

## 1. 当前状态

| 功能 | 状态 | 说明 |
|------|:--:|------|
| Sa-Token 认证 | ✅ 已实现 | 登录/登出/Token 刷新，BCrypt 密码加密 |
| Sentinel 限流 | ✅ 已实现 | `@PostConstruct` 硬编码加载规则，AOP 切面生效 |
| LDAP/SSO | ❌ 未实现 | 原方案标记「支持对接」，无代码 |
| Sentinel Dashboard | ❌ 未实现 | 原方案标记「可选」，规则通过代码管理 |

---

## 2. 方案一：LDAP/SSO 企业认证

### 2.1 设计目标

在现有 Sa-Token 认证基础上，新增 LDAP/SSO 作为**可选认证源**，不破坏现有登录流程。

```
用户登录
    │
    ├─ 本地账号？──→ Sa-Token 认证（现有逻辑，不变）
    │
    └─ 企业账号？──→ LDAP 认证 ──→ 签发 Sa-Token
                    │
                    └─ SSO (OAuth2/OIDC) ──→ 签发 Sa-Token
```

### 2.2 实现步骤

| 步骤 | 内容 | 涉及文件 |
|:--:|------|------|
| 1 | 新增 `AuthProviderType` 枚举（LOCAL/LDAP/SSO） | `domain/tenant/valueobject/` |
| 2 | 新增 `LdapConfig` 配置类（spring.ldap.*） | `infrastructure/config/` |
| 3 | 实现 `LdapAuthenticationProvider` | `infrastructure/security/ldap/` |
| 4 | 扩展 `AuthController.login()` 支持 provider 参数 | `interfaces/rest/AuthController.java` |
| 5 | 新增 `AuthProviderFactory` 策略工厂 | `application/security/` |

### 2.3 核心代码

```java
// AuthProviderType.java
public enum AuthProviderType {
    LOCAL,    // 本地数据库认证
    LDAP,     // 企业 LDAP
    SSO;      // OAuth2/OIDC

    public static AuthProviderType fromCode(String code) {
        try { return valueOf(code.toUpperCase()); }
        catch (IllegalArgumentException e) { return LOCAL; }
    }
}
```

```java
// LdapAuthenticationProvider.java
@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private final LdapTemplate ldapTemplate;

    @Override
    public AuthProviderType supportedType() {
        return AuthProviderType.LDAP;
    }

    @Override
    public User authenticate(String username, String password) {
        // 1. LDAP 搜索用户 DN
        String userDn = searchUserDn(username);
        // 2. 绑定验证
        boolean authenticated = ldapTemplate.authenticate(
            LdapQueryBuilder.query().base("ou=users").filter("uid={0}", username),
            password
        );
        // 3. 从 LDAP 属性映射到本地 User 实体
        if (authenticated) {
            return mapLdapAttributesToUser(username);
        }
        throw new AuthenticationException("LDAP 认证失败");
    }
}
```

```yaml
# application.yml 新增配置
spring:
  ldap:
    enabled: false          # 默认禁用
    urls: ldap://ldap.company.com:389
    base: dc=company,dc=com
    user-dn-pattern: uid={0},ou=users
```

### 2.4 阻塞点

需要企业提供 LDAP 服务器地址和绑定凭据。开发环境可通过 **Apache Directory Studio** 或 **OpenLDAP Docker** 搭建本地测试环境。

---

## 3. 方案二：Sentinel Dashboard

### 3.1 设计目标

将 Sentinel 规则从「代码硬编码」迁移到「Dashboard 可视化管理」，支持运行时动态修改规则。

### 3.2 实现步骤

| 步骤 | 内容 |
|:--:|------|
| 1 | Docker 启动 Sentinel Dashboard：`docker run -p 8089:8089 sentinel-dashboard` |
| 2 | 移除 `SentinelConfig.java` 中的 `@PostConstruct initRules()` |
| 3 | 新增 `application.yml` 配置连接 Dashboard |
| 4 | 通过 Dashboard 创建限流/熔断规则，实时生效 |

### 3.3 配置

```yaml
# application.yml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8089     # Dashboard 地址
        port: 8719                    # 与 Dashboard 通信端口
      datasource:
        ds1:
          nacos:                      # 规则持久化到 Nacos（可选）
            server-addr: localhost:8848
            data-id: sentinel-rules
            rule-type: flow
```

### 3.4 阻塞点

Sentinel Dashboard 需要独立进程运行。当前 Sentinel 规则通过代码硬编码已能正常工作，Dashboard 是运维增强，非紧急。

---

## 4. 实施建议

| 事项 | 建议 | 理由 |
|------|------|------|
| LDAP/SSO | 等企业部署环境确定后再实现 | 不同企业 LDAP Schema 差异大，需定制 |
| Sentinel Dashboard | 随 Docker Compose 一键部署环境一起搭 | 单独为 Dashboard 起一个进程不值得 |

**推荐路线**：先做 Docker Compose 环境（含 Sentinel Dashboard），再做 LDAP/SSO（需要企业方配合）。
