# Controller 层 Map 返回值重构为强类型 Response DTO

> **日期**: 2026-07-03 00:33
> **分支**: master
> **触发**: 代码生成（12 个新 Response DTO + 3 个 ApplicationService 修改 + 6 个 Controller 修改）

## 做了什么

将 6 个 Controller 中所有直接返回 `Map` 的方法重构为返回强类型 Response DTO，保证接口的可扩展性和编译期类型安全。

### 新增 12 个 Response DTO（application 层）

| 模块 | DTO | 替换 |
|------|-----|------|
| `application/approval/dto/` | `ApprovalStatsResponse` | `Map<String, Object>` stats |
| `application/task/dto/` | `TaskPlanResponse` | `Map<String, Object>` plan |
| `application/task/dto/` | `TaskExecuteResponse` | `Map<String, String>` execute |
| `application/task/dto/` | `TaskPlanDetailResponse` | `Map<String, Object>` getPlan |
| `application/task/dto/` | `TaskCancelResponse` | `Map<String, String>` cancel |
| `application/task/dto/` | `ActionHandlerResponse` | `Map<String, Object>` handler info |
| `application/knowledge/dto/` | `KbFileListResponse` | `Map<String, Object>` file list |
| `application/knowledge/dto/` | `KnowledgeBaseStatsResponse` | `Map<String, Long>` KB stats / file summary |
| `application/knowledge/dto/` | `DocumentChunkListResponse` | `Map<String, Object>` chunk list |
| `application/knowledge/dto/` | `BatchParseResponse` | `Map<String, Object>` batch parse |
| `application/knowledge/dto/` | `StrategyPresetResponse` | `Map<String, Object>` strategy preset |

### 修改 3 个 ApplicationService（从源头返回 DTO）

- `ApprovalWorkflowApplicationService.stats()` → 返回 `ApprovalStatsResponse`
- `KnowledgeBaseApplicationService.getStats()` → 返回 `KnowledgeBaseStatsResponse`
- `PrecisionConfigApplicationService.listStrategyPresets()` → 返回 `List<StrategyPresetResponse>`

### 修改 6 个 Controller

- `ApprovalController` — 1 方法
- `TaskController` — 5 方法
- `FileManagementController` — 2 方法
- `DocumentController` — 2 方法
- `KnowledgeSearchController` — 1 方法
- `KnowledgeBaseController` — 1 方法

## 关键决策

- Response DTO 统一放在 **application 层** `dto/` 包中，遵循项目现有约定
- DTO 使用 `@Data` + `@Builder` + `@NoArgsConstructor` + `@AllArgsConstructor` + `@Schema` 一致性风格
- ApplicationService 也一并重构，从源头杜绝 Map 返回
- `KbFileListResponse` 包含嵌套 `KbInfo` 内部类（小而内聚，不单独提文件）
- `KnowledgeBaseStatsResponse` 同时服务于 `FileManagementController.summary()` 和 `KnowledgeBaseController.getStats()` 两个端点

## 规范落地

- 更新 `docs/开发规范.md` §1.8（前后端规约）+ §9.2（禁止事项）
- 更新 `CLAUDE.md` 踩坑记录 #14（强制规则）

## 下一步

- 编译通过 ✅ BUILD SUCCESS（7/7 模块）
- 建议后续 Controller 开发时严格遵循此规范
