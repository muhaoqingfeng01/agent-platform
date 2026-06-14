# RBAC 权限模型

## 所属阶段
**P0 基础底座 → T8 多租户数据与存储**

## 使用技术
- Sa-Token `@SaCheckPermission` + `@SaCheckRole` + `@SaCheckLogin`
- 数据库 RBAC 五表模型

## 涉及数据库表
- `t_role`, `t_permission`, `t_user_role`, `t_role_permission`

## 数据模型

```
t_user ──N:M── t_user_role ──N:1── t_role ──1:N── t_role_permission ──N:1── t_permission
```

## 预设角色

| 角色编码 | 角色名 | 权限范围 |
|----------|--------|----------|
| `TENANT_ADMIN` | 租户管理员 | 本租户全部权限 |
| `OPERATOR` | 运营人员 | Agent 配置、提示词管理、知识库管理 |
| `DEVELOPER` | 开发者 | 工具注册、意图配置、评估管理 |
| `APPROVER` | 审批人 | 审批处理 + 基础查看 |
| `VIEWER` | 查看者 | 只读权限 |

## 预设权限

| 权限编码 | 资源 | 操作 | 说明 |
|----------|------|------|------|
| `tenant:admin` | tenant | ADMIN | 租户管理 |
| `user:write` | user | WRITE | 用户创建/编辑 |
| `user:read` | user | READ | 用户查看 |
| `agent:write` | agent | WRITE | Agent 配置编辑 |
| `agent:read` | agent | READ | Agent 查看 |
| `conversation:write` | conversation | WRITE | 创建会话 |
| `knowledge:write` | knowledge | WRITE | 知识库管理 |
| `tool:write` | tool | WRITE | 工具注册 |
| `prompt:write` | prompt | WRITE | 提示词编辑 |
| `prompt:publish` | prompt | PUBLISH | 提示词发布 |
| `evaluation:write` | evaluation | WRITE | 评估管理 |
| `approval:handle` | approval | WRITE | 审批处理 |
| `audit:read` | audit | READ | 审计日志查看 |

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST | `/api/v1/roles` | 角色列表 / 创建 |
| PUT/DELETE | `/api/v1/roles/{id}` | 更新 / 删除角色 |
| GET/POST | `/api/v1/permissions` | 权限列表 / 创建 |
| POST | `/api/v1/users/{id}/roles` | 为用户分配角色 |
| GET | `/api/v1/users/{id}/roles` | 查看用户角色 |
| POST | `/api/v1/roles/{id}/permissions` | 为角色分配权限 |

## 实现方案

### 权限加载（Sa-Token StpInterface）

Sa-Token 通过 `StpInterface` 接口自动加载用户权限和角色，每次鉴权时按需调用。

```java
@Component
public class StpInterfaceImpl implements StpInterface {

    private final PermissionService permissionService;
    private final RoleService roleService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // loginId = user.getUserId()，登录时通过 StpUtil.login(userId) 设置
        return permissionService.getPermissionCodes((String) loginId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return roleService.getRoleCodes((String) loginId);
    }
}
```

### 登录时写入用户上下文

```java
@PostMapping("/login")
public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    User user = userService.authenticate(request.getUsername(), request.getPassword());
    if (user == null) {
        return Result.fail("用户名或密码错误");
    }
    
    // Sa-Token 登录
    StpUtil.login(user.getUserId());
    
    // 将租户信息存入 Sa-Token Session（轻量数据）
    StpUtil.getSession().set("tenantId", user.getTenantId());
    StpUtil.getSession().set("username", user.getUsername());
    
    return Result.ok(new LoginResponse(StpUtil.getTokenValue()));
}
```

### 权限变更通知

角色/权限变更后，强制相关用户重新登录（使其重新加载权限）：

```java
// 权限变更时踢出相关用户
public void onPermissionChanged(String userId) {
    StpUtil.kickout(userId);  // 强制下线，下次登录自动重新加载权限
}
```
