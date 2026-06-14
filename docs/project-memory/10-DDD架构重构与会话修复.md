# 10-DDD架构重构与会话修复

> **日期**: 2026-06-14
> **分支**: master
> **触发**: ChatClient Bean 修复 + DTO 抽取 + DDD 分层强约束 + 用户权限领域建模
> **关联**: [[11-DDD架构强制约束]] — 本次重构提炼的强制规则

## 做了什么

### 1. ChatClient Bean 修复（3 个坑）
- 新增 `spring-ai-autoconfigure-model-chat-client` + `spring-ai-autoconfigure-model-deepseek` 依赖
- 创建 `AiConfig.java` — Spring AI 只自动配置 `ChatClient.Builder`，需手动 `@Bean ChatClient`
- YAML 从 DashScope → DeepSeek（`spring-ai-alibaba-bom:1.1.2.0` 不含 DashScope 自动配置）

### 2. Controller DTO 抽取
- **Application 层**: 16 个 DTO（Tenant/User/Role/Permission 的 Request + Response）从 Controller 内嵌类移出
- **Interfaces 层**: 10 个 DTO（Intent/Conversation/Message 的 Request）保留在 interfaces，Controller 手动映射到 Application DTO
- 关键规则：Application 层不能 import interfaces 层（DDD 依赖方向）

### 3. DDD 分层强约束
- 新建 4 个 Application Service：`TenantApplicationService`、`UserApplicationService`、`RoleApplicationService`、`PermissionApplicationService`
- 修复前：4 个 Controller 直接注入 Repository（越层调用）
- 修复后：Controller → ApplicationService → Repository（三层正交）

### 4. 用户权限领域建模
- 4 个 Domain Service：`TenantDomainService`、`UserDomainService`、`RoleDomainService`、`PermissionDomainService`
- 聚合关系：User(聚合根) → Role(通过 t_user_role) → Permission(通过 t_role_permission)
- 核心不变式：新用户注册必须分配默认 VIEWER 角色；权限变更须强制用户下线

## 关键决策

1. **Response DTO 归属 Application 层** — 作为应用服务输出契约，避免 circular dependency
2. **Request DTO 也归属 Application 层** — 同理，Application Service 方法签名必须用自身包的 DTO
3. **Controller 映射层** — Intent/Conversation/Message 的 interfaces DTO 通过 `toAppRequest()` 手动映射

## 踩坑记录

1. **`mvn compile` ≠ `mvn install`** — 新增 pom 依赖后必须 install，否则 bootstrap 解析不到传递依赖
2. **`spring-ai-alibaba-agent-framework` 不含 ChatModel** — 只是 Agent 编排框架，需单独加模型依赖
3. **Application 层不能 import interfaces 层** — 违反 DDD 依赖方向，Response DTO 必须下沉到 Application 层

## 当前状态

- P0: 🟡 ~85%（ServiceImpl 切换待完成）
- P1-T3: 🟡 ~80%（新增 DDD 分层重构完毕）
- Controller 代码：8 个 Controller 总计 803 行（纯 HTTP 适配层）
- DTO：26 个独立 DTO 文件（16 在 Application 层 + 10 在 Interfaces 层）

## 下一步

1. P0 收尾：ServiceImpl 从 Stub 切到 RepositoryImpl
2. P1-T3 联调：端到端测试意图识别链路
3. P1-T4 启动：提示词管理与版本控制
