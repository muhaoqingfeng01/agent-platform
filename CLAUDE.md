# CLAUDE.md — Agent Platform 项目记忆

> 此文件由 Claude Code 在每次会话启动时自动加载。
> 详细记忆文件: `docs/project-memory/`
> 快照脚本: `.claude/hooks/session-snapshot.py`
> Session Log: `docs/project-memory/sessions/.session-log.jsonl`

---

## 🤖 对话快照规则（每次会话必须遵守）

**核心原则: 每个重大任务完成后，立即将关键信息写入 `docs/project-memory/`。**

### 触发条件（满足任一即快照）

| 触发条件 | 快照内容 | 文件命名 |
|----------|----------|----------|
| **代码生成完成**（新增/修改 >3 个文件） | 改了什么文件、关键设计决策、踩坑 | `XX-任务名.md` |
| **Bug 修复完成** | 根因、修复方案、预防措施 | `XX-任务名.md` |
| **架构决策**（选型/方案变更） | 决策背景、备选方案、选择理由 | `XX-任务名.md` |
| **会话结束前**（Stop Hook 触发） | 本次会话总结：做了什么、还有什么没做、下一步 | `XX-会话总结-YYYY-MM-DD.md` |
| **每 30 分钟无快照** | 当前进度快照 | `XX-进度快照.md` |

### 快照文件格式

```markdown
# [标题]

> **日期**: YYYY-MM-DD HH:MM
> **分支**: master
> **触发**: [代码生成 | Bug修复 | 架构决策 | 会话总结 | 进度快照]

## 做了什么
- ...

## 关键决策
- ...

## 踩坑记录
- ...

## 下一步
- ...
```

### 快照后必须更新索引

每次写入新快照后，**必须同步更新** `docs/project-memory/README.md` 的会话列表。

### 🔴 代码完成后自动同步（强制）

**每次代码开发任务完成后，无需等待用户提醒，自动执行以下同步：**

| # | 同步文件 | 更新内容 |
|:--:|------|------|
| 1 | `docs/开发进度.md` | 模块状态、功能清单、API 端点、代码统计、下一步 |
| 2 | `docs/project-memory/00-项目现状摘要.md` | 代码文件数、开发进度表、日期 |
| 3 | `CLAUDE.md` | 当前阶段、文件数、优先级图 ✅ 标记、待完成列表 |
| 4 | `docs/project-memory/XX-任务名.md` | 实现快照：做了什么、关键决策、踩坑 |
| 5 | `docs/project-memory/XX-会话总结-YYYY-MM-DD.md` | 会话总结 |
| 6 | `docs/project-memory/README.md` | 新增会话条目 |
| 7 | `memory/MEMORY.md` + 独立文件 | 关键技术决策、架构模式、踩坑 |

> 📋 详见记忆: [[auto-sync-progress-and-memory]]

### Stop Hook 自动机制

- `Stop` 事件 → 运行 `.claude/hooks/session-snapshot.py`
- **仅当有实际文件变更时**才写入 `.session-log.jsonl` 和 `session-*.json`
- 无变更的会话不产生任何日志文件
- **会话结束时你必须额外写一份 `XX-会话总结-YYYY-MM-DD.md`**，不要只依赖 hook

---

## 🔴 DDD 分层架构 — 强制约束

**所有后续代码开发与迭代必须严格遵守以下规则：**

```
interfaces → application → domain ← infrastructure
```

| 规则 | 说明 |
|------|------|
| **禁止越层调用** | Controller 绝不能直接注入 Repository |
| **Application 层不 import interfaces 层** | DTO 必须下沉到 Application 层 |
| **DomainService 封装业务规则** | 领域不变式不得泄漏到 Application 层 |
| **新功能流程** | Domain 建模 → Repository 接口 → DomainService → ApplicationService → Controller |
| **反模式检测** | 见 `[[11-DDD架构强制约束]]` 完整清单 |

> 📋 详细规范：`docs/project-memory/11-DDD架构强制约束.md`

---

## 项目定位

**企业级 AI Agent 平台** — DDD 六模块 Maven 多模块项目，P0-P4 核心已全部实现（~387 Java 文件），P5 前端未开始，P6 迭代增强待执行。

- **路径**: `D:\mhqf_project\heavenly-craft-agent\agent-platform`
- **包名**: `com.example.agent`
- **模块**: common → domain → application → infrastructure → interfaces → bootstrap

---

## 环境速查

| 组件 | 版本 | 备注 |
|------|------|------|
| JDK | **17.0.18** | `C:\tools\Java\jdk-17.0.18` |
| Spring Boot | **3.3.7** | 不要升级到 3.5.x |
| Spring Cloud Alibaba | 2023.0.3.2 | |
| Spring AI Alibaba | 1.1.2.0 | groupId=`com.alibaba.cloud.ai`（含 `.ai`） |
| Spring AI | 1.1.7 | |
| MySQL Connector | **8.0.33** | 不要用 3.0.33 或 9.x |
| Maven 镜像 | `https://maven.aliyun.com/repository/public/` | |
| 本地仓库 | `D:\tools\repository` | |
| 编译状态 | ✅ BUILD SUCCESS（7/7 模块） | |

---

## ⚠️ 踩坑记录（必读）

1. **Spring AI Alibaba 的 groupId 是 `com.alibaba.cloud.ai`**（不是 `com.alibaba.cloud`）
2. **`spring-ai-alibaba-starter` 已废弃** → 用 `spring-ai-alibaba-agent-framework`
3. **`spring-ai-mcp-client-spring-boot-starter`** → 正确名称是 `spring-ai-starter-mcp-client`
4. **`spring-ai-rag-core` 不存在** → 正确名称是 `spring-ai-rag`
5. **Langfuse 用 HTTP Ingestion API 直连**（不依赖 langfuse-java SDK，其 0.2.0 版本是 auto-generated OpenAPI 客户端，无高层 API）
6. **Swagger 配置在 interfaces 模块**，不在 infrastructure（会缺依赖）
7. **MySQL 用 8.0.33** 不是 3.0.33（那个版本不存在）
8. **Spring AI 只自动配置 `ChatClient.Builder`**，`ChatClient` 需手动 `@Bean` 包装（见 `AiConfig.java`）
9. **必须显式添加 `spring-ai-autoconfigure-model-deepseek` + `spring-ai-autoconfigure-model-chat-client`**，否则 ChatModel/ChatClient.Builder 均不会创建
10. **`spring-ai-alibaba-agent-framework` 不含 ChatModel**，需单独引入模型依赖（项目用 DeepSeek）
11. **新增依赖后必须 `mvn install`**，否则 bootstrap 模块解析不到传递依赖
12. **Application 层禁止 import interfaces 层** — DTO 必须下沉到 Application 层，否则循环依赖
13. **🔴 枚举统一规范（强制）** — 所有枚举必须有 `code` + `desc` 两个字段，使用 `fromCode(code)` 获取枚举，使用 `getCode()` 序列化，**禁止使用 `name()` 进行枚举比较或 `valueOf()` 转换**
    - 格式: `@Getter @AllArgsConstructor public enum Xxx { VALUE("VALUE", "中文描述"); private final String code; private final String desc; }`
    - code 必须与枚举常量名一致（`name()`）
    - 所有枚举强制包含 `fromCode(String code)` 工厂方法，通过 `e.code.equalsIgnoreCase(code)` 比较
    - Repository 中 toDomain 用 `Xxx.fromCode(po.getXxx())`，toPO 用 `entity.getXxx().getCode()`
    - 同类型枚举比较用 `==` 直接比较；String→枚举用 `fromCode()`；枚举→String 用 `getCode()`

---

## 当前 Java 代码（~410 个文件，P0 + P1 + P2 + P3 + P4 + P6 核心已实现）

```
agent-platform-bootstrap/    1 文件  ← @SpringBootApplication + @EnableAsync
agent-platform-common/       9 文件  ← Result、6 异常、PageResponse、IdGenerator
agent-platform-domain/     103 文件  ← 23 实体 + 23 仓储接口 + 32 值对象 + 5 安全接口 + 19 DomainService/端口
agent-platform-application/ 105 文件  ← 19 AppService + 3 识别器 + 5 提取器 + 4 Resolver + 5 Handler + 7 切片策略 + 1 管线 + 10 Security DTO + 2 Event + ...
agent-platform-infrastructure/ 113 文件 ← 23 PO + 23 Mapper + 23 Impl + 3 ServiceImpl + 13 Config + 4 Rag + 2 Observability + AgentMetrics + McpClientManager + HttpToolAdapter + 3 Annotation + 2 Aspect + ...
agent-platform-interfaces/   55 文件  ← 20 Controller + 28 Request DTO + ExceptionHandler + SwaggerConfig + 4 认证 DTO
```

> ✅ 已实现：多租户 RBAC、意图识别 3 层链、对话管理、SSE/WebSocket 流式、状态机、长期记忆、T4 提示词管理、T5 任务规划引擎、T6 RAG 知识库、T7 MCP 工具平台、T10 安全围栏、T11 人机协同审批、T9 全链路可观测性、T12 效果评估与持续优化、**P6 迭代增强（Redis缓存/Reranker/工具版本化/心跳检测/精度监控/LDAP/SSO/Presidio）**
> 📐 DDD 架构：Controller → ApplicationService → DomainService → Repository，禁止越层调用
> 📦 DTO 分离：Application 层 DTO 独立分包 + Interfaces 层 Request DTO 独立分包
> 🔜 待完成：P5 前端交互层

---

## 数据库（28 张表 + V1.4.0 + V1.2.2，手动管理）

- **V1.0.0** (13张): t_tenant, t_user, t_role, t_permission, t_user_role, t_role_permission, t_agent_config, t_conversation, t_message, t_knowledge_base, t_tool_registry, t_prompt_template, t_evaluation_run
- **V1.1.0** (15张): t_intent, t_long_term_memory, t_prompt_template_version, t_task_execution, t_task_step_execution, t_document, t_document_chunk, t_knowledge_hit_record, t_tool_invocation_log, t_sensitive_word, t_security_event, t_audit_log, t_approval_workflow, t_evaluation_dataset, t_evaluation_dataset_item, t_optimization_ticket
- **V1.2.0**: 管理员种子数据 (admin/Mhqf@123456)
- **V1.2.1**: 业务 ID 字段补充（conversation/message/intent/long_term_memory 表）
- **V1.2.2**: 🆕 T7 工具调用日志业务 ID（t_tool_invocation_log.invocation_id）
- **V1.3.0**: T6-RAG: t_knowledge_base 14 个精度控制字段
- **V1.4.0**: 🆕 KB 文件管理升级: created_by + 状态迁移 + chunk.deleted

---

## 开发优先级

```
P0(收尾✅) → P1(T3-T5✅) → P2(T6-T7✅) → P3(安全✅) → P4(观测✅核心) → P6(增强✅) → P5(前端⬜)
统一网关✅     意图识别✅      RAG引擎✅     安全围栏✅    全链路✅核心   观测增强🔜     交互端⬜
多租户✅       提示词管理✅    MCP平台✅     人机协同✅    效果评估✅     运维部署🔜
              任务规划✅
```

**P5**: 前端交互层（Web聊天/审批卡片/反馈/IM），与后端并行开发
**P6**: 迭代优化（7 项观测增强 + Docker/K8s 部署），方案已设计

### ⚠️ 已知差距速览

| 类别 | 数量 | 说明 |
|------|:--:|------|
| 代码功能缺口 | 8 项 | Reranker/精度监控/工具版本化/Redis缓存等 |
| 运维设施缺口 | 11 项 | Grafana/OTel/ELK/AlertManager/Docker/K8s 等 |
| 方案变更 | 5 项 | 均为「简化设计」方向，详见 `docs/开发进度.md` |

---

## 关键文档索引

| 文档 | 路径 |
|------|------|
| 项目现状摘要 | `docs/project-memory/00-项目现状摘要.md` |
| 会话索引 | `docs/project-memory/README.md` |
| 开发进度 | `docs/开发进度.md` |
| P0-P4 差距分析 | `docs/project-memory/XX-P0-P4-差距分析-2026-06-18.md` |
| 开发规范 | `docs/开发规范.md` |
| 后端开发计划 | `docs/后端开发计划.md` |
| 数据库设计 | `docs/数据库设计文档.md` |
| 技术方案汇总 | `docs/企业级Agent平台技术方案.md` |
| 技术方案流程图 | `docs/Agent平台技术方案流程图.md` | 🆕 11 张 Mermaid 图 |
| 架构设计图 | `docs/Agent平台架构设计图.md` | 🆕 10 张架构图 (4+1/部署/DDD/安全/MCP) |
| 子方案(P0-P5) | `docs/P0-基础底座/` ~ `docs/P5-交互端/` |
| P6 迭代增强 | `docs/P6-迭代优化方案/` |
| Swagger 配置 | `agent-platform-interfaces/.../config/OpenApiConfig.java` |
| 数据库迁移SQL | `docs/database/` |

---

## 启动依赖的外部服务

MySQL(`:3306`) + Redis(`:6379`) + Milvus(`:19530`) + MinIO(未配置)

## Swagger

`http://localhost:8080/swagger-ui.html` — Sa-Token Bearer 鉴权
