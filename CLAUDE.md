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

**企业级 AI Agent 平台** — DDD 六模块 Maven 多模块项目，P2 阶段（T6 RAG + T7 MCP 已完成）。

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
5. **langfuse-java 最新是 0.2.0**（不是 2.0.4）
6. **Swagger 配置在 interfaces 模块**，不在 infrastructure（会缺依赖）
7. **MySQL 用 8.0.33** 不是 3.0.33（那个版本不存在）
8. **Spring AI 只自动配置 `ChatClient.Builder`**，`ChatClient` 需手动 `@Bean` 包装（见 `AiConfig.java`）
9. **必须显式添加 `spring-ai-autoconfigure-model-deepseek` + `spring-ai-autoconfigure-model-chat-client`**，否则 ChatModel/ChatClient.Builder 均不会创建
10. **`spring-ai-alibaba-agent-framework` 不含 ChatModel**，需单独引入模型依赖（项目用 DeepSeek）
11. **新增依赖后必须 `mvn install`**，否则 bootstrap 模块解析不到传递依赖
12. **Application 层禁止 import interfaces 层** — DTO 必须下沉到 Application 层，否则循环依赖

---

## 当前 Java 代码（~301 个文件，P0 + P1 + P2-T6 + P2-T7 已实现）

```
agent-platform-bootstrap/    1 文件  ← @SpringBootApplication + @EnableAsync
agent-platform-common/       8 文件  ← Result、5 异常、PageResponse、IdGenerator
agent-platform-domain/      85 文件  ← 16 实体 + 16 仓储接口 + 25 值对象 + 5 安全接口 + 19 DomainService/端口
agent-platform-application/ 78 文件  ← 15 AppService + 3 识别器 + 5 提取器 + 4 Resolver + 5 Handler + 7 切片策略 + 1 管线 + 6 Tool DTO + ...
agent-platform-infrastructure/ 81 文件 ← 16 PO + 16 Mapper + 16 Impl + 3 ServiceImpl + 11 Config + 4 Rag + McpClientManager + HttpToolAdapter + ...
agent-platform-interfaces/   48 文件  ← 15 Controller + 25 Request DTO + ExceptionHandler + SwaggerConfig + 4 认证 DTO
```

> ✅ 已实现：多租户 RBAC、意图识别 3 层链、对话管理、SSE/WebSocket 流式、状态机、长期记忆、T4 提示词管理、T5 任务规划引擎、T6 RAG 知识库、T7 MCP 工具平台
> 📐 DDD 架构：Controller → ApplicationService → DomainService → Repository，禁止越层调用
> 📦 DTO 分离：Application 层 DTO 独立分包 + Interfaces 层 Request DTO 独立分包
> 🔜 待完成：安全围栏(P3-T10)、人机协同审批(P3-T11)、全链路观测(P4-T9)、效果评估(P4-T12)

---

## 数据库（28 张表 + V1.4.0 + V1.2.2，Flyway 管理）

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
P0(收尾) → P1(T3-T5) → P2(T6-T7) → P3(安全) → P4(观测) → P5(前端独立)
统一网关     意图识别✅   RAG引擎✅   安全围栏     全链路      交互端
多租户       对话管理✅   MCP平台✅   人机协同     效果评估
            提示词管理✅
            任务规划引擎✅
```

**P5**: 前端交互层（Web聊天/审批卡片/反馈/IM），与后端并行开发

---

## 关键文档索引

| 文档 | 路径 |
|------|------|
| 项目现状摘要 | `docs/project-memory/00-项目现状摘要.md` |
| 会话索引 | `docs/project-memory/README.md` |
| 开发进度 | `docs/开发进度.md` |
| 开发规范 | `docs/开发规范.md` |
| 后端开发计划 | `docs/后端开发计划.md` |
| 数据库设计 | `docs/数据库设计文档.md` |
| 技术方案汇总 | `docs/企业级Agent平台技术方案.md` |
| 子方案(P0-P5) | `docs/P0-基础底座/` ~ `docs/P5-交互端/` |
| Swagger 配置 | `agent-platform-interfaces/.../config/OpenApiConfig.java` |
| Flyway 迁移 | `agent-platform-bootstrap/.../db/migration/` |

---

## 启动依赖的外部服务

MySQL(`:3306`) + Redis(`:6379`) + Milvus(`:19530`) + MinIO(未配置)

## Swagger

`http://localhost:8080/swagger-ui.html` — Sa-Token Bearer 鉴权
