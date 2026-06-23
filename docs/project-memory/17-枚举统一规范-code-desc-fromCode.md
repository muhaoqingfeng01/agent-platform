---
name: enum-code-desc-pattern
description: 🔴 强制规范：所有枚举必须包含 code+desc 字段，使用 fromCode/getCode 替代 name/valueOf
metadata:
  type: project
---

# 枚举统一规范（强制）

> **状态**: 🔴 强制约束，所有新代码必须遵守
> **生效日期**: 2026-06-24
> **影响范围**: domain 层全部 33 个枚举 + 所有 RepositoryImpl

## 规则

### 1. 枚举定义格式

所有枚举必须包含 `code` 和 `desc` 两个 final 字段：

```java
@Getter
@AllArgsConstructor
public enum XxxEnums {
    VALUE1("VALUE1", "中文描述"),
    VALUE2("VALUE2", "中文描述");

    private final String code;   // 必须与枚举常量名一致
    private final String desc;   // 中文业务描述

    public static XxxEnums fromCode(String code) {
        if (code == null || code.isBlank()) return DEFAULT;
        for (XxxEnums e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
```

### 2. code 与 name() 的关系

**code 必须与枚举常量名（name()）保持一致**。即 `VALUE1("VALUE1", ...)`。

### 3. 枚举获取方式

| 场景 | ✅ 正确 | ❌ 禁止 |
|------|---------|---------|
| String → 枚举 | `Xxx.fromCode(str)` | `Xxx.valueOf(str)` |
| 枚举 → String | `x.getCode()` | `x.name()` |
| 枚举间比较 | `x == Xxx.VALUE1` | `x.name().equals("VALUE1")` |
| String 比较 | `x.getCode().equals(str)` | `x.name().equals(str)` |

### 4. Repository 中用法

```java
// toDomain (PO → Entity): 使用 fromCode()
.status(po.getStatus() != null ? UserStatusEnums.fromCode(po.getStatus()) : null)

// toPO (Entity → PO): 使用 getCode()
.status(user.getStatus() != null ? user.getStatus().getCode() : null)
```

### 5. 为什么用 fromCode/getCode 而不是 name/valueOf

- `name()` 是 JVM 内部标识，不应暴露到业务层
- `valueOf()` 抛出 IllegalArgumentException 且不支持 null-safe
- `fromCode()` 可以按业务规则处理（如 null 返回默认值、大小写不敏感）
- `getCode()` 为未来 code ≠ name 的场景预留灵活性（尽管当前要求一致）

## 已迁移枚举清单（33 个）

| 包 | 枚举 | code 示例 |
|-----|------|-----------|
| `tenant.valueobject` | `UserStatusEnums`, `TenantStatusEnums`, `TenantTierEnums` | ACTIVE/SUSPENDED/STANDARD |
| `evaluation.valueobject` | `EvaluationRunStatusEnums` | PENDING/RUNNING/COMPLETED |
| `audit.valueobject` | `ActorTypeEnums` | USER/SYSTEM |
| `task.valueobject` | `ExecutionStatus`, `StepStatus` | PENDING/RUNNING/COMPLETED/FAILED |
| `security.valueobject` | `ApprovalStatus`, `MatchType`, `SensitiveCategory`, `SeverityLevel`, `ActionType`, `SensitiveWordStatus`, `AuthProviderType` | PENDING/EXACT/INJECTION/LOW/LOG/ACTIVE |
| `optimization.valueobject` | `TicketStatus` | OPEN/ANALYZING/IN_PROGRESS/RESOLVED |
| `conversation.valueobject` | `ConversationStatus`, `MessageRole`, `IntentStatus`, `IntentCategory`, `MemoryType`, `FeedbackType` | ACTIVE/USER/FAQ/FACT/LIKE |
| `prompt.valueobject` | `PromptStatus` | DRAFT/PUBLISHED/ARCHIVED |
| `knowledge.valueobject` | `KnowledgeBaseStatus`, `DocumentStatus`, `ChunkStrategy`, `IndexType`, `MetricType`, `ConsistencyLevel`, `SearchStrategy`, `RerankerType` | ENABLED/PENDING_PARSE/IVF_FLAT/COSINE |
| `tool.valueobject` | `ToolType`, `ToolStatus`, `InvocationStatus` | MCP/HTTP/BUILTIN/CUSTOM |

## 关联

- [[11-DDD架构强制约束]]
- [[ddd-project-conventions]]
- [[coding-standards]]
