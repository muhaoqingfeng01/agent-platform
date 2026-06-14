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

## 项目定位

**企业级 AI Agent 平台** — DDD 六模块 Maven 多模块项目，当前为脚手架阶段（零业务逻辑）。

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

---

## 当前 Java 代码（18 个文件，零业务逻辑）

```
AgentPlatformApplication.java          ← @SpringBootApplication 入口
AgentPlatformApplicationTests.java     ← 空 @SpringBootTest
16 个 package-info.java                ← 各包 Javadoc
```

> ⚠️ 所有 Controller/Service/Repository/Entity 均未实现。

---

## 数据库（28 张表，Flyway 管理）

- **V1.0.0** (13张): t_tenant, t_user, t_role, t_permission, t_user_role, t_role_permission, t_agent_config, t_conversation, t_message, t_knowledge_base, t_tool_registry, t_prompt_template, t_evaluation_run
- **V1.1.0** (15张，未执行): t_intent, t_long_term_memory, t_prompt_template_version, t_task_execution, t_task_step_execution, t_document, t_document_chunk, t_knowledge_hit_record, t_tool_invocation_log, t_sensitive_word, t_security_event, t_audit_log, t_approval_workflow, t_evaluation_dataset, t_evaluation_dataset_item, t_optimization_ticket

> 注：T1-T12 目录已废弃，全部技术方案已拆分至 P0-P5 目录下。详见 `docs/project-memory/05-对话自动快照系统.md` 及后续会话记录。

---

## 开发优先级

```
P0(5天) → P1(8天) → P2(6天) → P3(3天) → P4(4天) → P5(前端独立)
统一网关    意图识别    RAG引擎    安全围栏    全链路      交互端
多租户      提示词管理   MCP平台    人机协同    效果评估
          任务规划
```

**P0 先做**: 多租户 + 安全认证(Sa-Token) — 所有功能的基础底座

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
