---
name: ddd-architecture-constraint
description: 强制 DDD 分层架构约束 — 所有代码开发与迭代必须遵守
metadata:
  type: project
---

# DDD 分层架构强制约束

**规则：所有后续代码开发、迭代、重构必须严格遵守 DDD 四层架构。**

## 分层架构

```
┌────────────────────────────────────────────┐
│  interfaces (接口层)                        │
│  只做 HTTP 适配：参数校验、DTO 映射、调用 AppService │
│  禁止：直接调用 Repository、包含业务逻辑      │
├────────────────────────────────────────────┤
│  application (应用层)                       │
│  编排业务流程、事务管理、发布领域事件         │
│  定义本层 DTO（Request/Response）作为契约    │
│  禁止：直接操作数据库、import interfaces 层  │
├────────────────────────────────────────────┤
│  domain (领域层) ← 核心，不依赖任何上层      │
│  实体、值对象、聚合根、DomainService、仓储接口 │
│  封装业务规则与不变式                        │
├────────────────────────────────────────────┤
│  infrastructure (基础设施层)                 │
│  实现 domain 层接口：Repository、外部 API、消息 │
│  Config、Filter、Interceptor、AOP            │
└────────────────────────────────────────────┘
```

## 核心规则

1. **依赖方向**: interfaces → application → domain ← infrastructure（domain 是中心，不依赖任何层）
2. **禁止越层调用**: Controller 绝不能直接注入 Repository
3. **DTO 归属**: Application 层定义自己的 Request/Response DTO，interfaces 层的 DTO 必须手动映射后传入
4. **Application 层不能 import interfaces 层的任何类**（违反依赖方向）
5. **DomainService 封装业务规则**: 如 `UserDomainService.assignDefaultRole()` — 这是用户聚合根的不变式
6. **聚合根**: User 是聚合根，Role 和 Permission 通过关联表挂载，修改权限须通过 User 聚合操作
7. **新功能开发流程**: Domain 建模 → Repository 接口 → DomainService → ApplicationService → Controller

## 反模式（禁止）

- ❌ Controller 直接注入 `*Repository`
- ❌ Application 层 import `com.example.agent.interfaces.*`
- ❌ 领域逻辑写在 Controller 中（如密码校验、角色分配）
- ❌ 跨层直接操作数据库
- ❌ 贫血领域模型（只有 getter/setter 的实体）

## 检查清单

- [ ] Controller 只注入 ApplicationService（或 DomainService 接口）
- [ ] ApplicationService 的方法签名只用本包 DTO + Domain 类型
- [ ] DomainService 封装了核心业务规则
- [ ] 新 DTO 放在正确的层（Application 层 vs Interfaces 层）
- [ ] 编译通过：`mvn compile -q`

**Why:** 维护代码可测试性、可维护性、可扩展性。DDD 分层确保业务逻辑不泄漏到 HTTP 层。

**How to apply:** 每次写新功能前先画聚合关系，确定 Repository 接口 → DomainService → ApplicationService → Controller 的调用链。
