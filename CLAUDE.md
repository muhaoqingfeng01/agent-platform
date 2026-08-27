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

**企业级 AI Agent 平台** — DDD 六模块 Maven 多模块项目，P0-P4 核心已全部实现（573 Java 文件），P5 前端未开始，P6 迭代增强代码已实现（9 项），配置治理子方案 01-05 已全部实现，运维设施待部署（11 项）。

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
| MyBatis Plus | 3.5.9 | |
| MyBatis Spring Boot | 3.0.4 | |
| Sa-Token | 1.39.0 | |
| Redisson | 3.37.0 | |
| Hutool | 5.8.32 | |
| MapStruct | 1.6.3 | |
| Milvus SDK | 2.6.9 | |
| MinIO Client | 8.5.10 | |
| Apache Tika | 2.9.2 | 文档解析 |
| Guava | 33.3.1-jre | |
| Maven 镜像 | `https://maven.aliyun.com/repository/public/` | |
| 本地仓库 | `D:\tools\repository` | |
| 编译状态 | ✅ BUILD SUCCESS（7/7 模块） | |

---

## 🛠️ 开发命令速查

| 操作 | 命令 |
|------|------|
| 编译全部模块 | `mvn clean compile` |
| 安装到本地仓库（跳过测试） | `mvn clean install -DskipTests` |
| 打包可执行 JAR | `mvn clean package -DskipTests -pl agent-platform-bootstrap` |
| 启动应用（dev） | `mvn spring-boot:run -pl agent-platform-bootstrap` |
| 运行单个测试 | `mvn test -pl <module> -Dtest=<TestClass>` |
| 查看依赖树 | `mvn dependency:tree -pl <module>` |

**⚠️ 注意**: 项目无 Maven Wrapper（`mvnw`），需系统安装 Maven 3.9+。新增依赖后必须 `mvn install`，否则 bootstrap 模块解析不到传递依赖。

**外部服务**: 启动前确保 MySQL(`:3306`)、Redis(`:6379`)、Milvus(`:19530`) 已运行。MinIO 和 Langfuse 为可选服务。

**🔒 安全提醒**: `application.yml` 中的 API Key 和密码仅作本地开发默认值，**务必通过环境变量覆盖**生产环境配置：
- `DEEPSEEK_API_KEY` / `EMBEDDING_API_KEY` — AI 模型密钥
- `MYSQL_PASSWORD` / `REDIS_PASSWORD` — 数据库密码
- `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` — 对象存储凭证
- `LANGFUSE_PUBLIC_KEY` / `LANGFUSE_SECRET_KEY` — 可观测性密钥

**Swagger**: `http://localhost:8080/swagger-ui.html` — Sa-Token Bearer 鉴权

**仅 1 个测试文件**（`bootstrap/.../AgentPlatformApplicationTests.java`），无单元测试覆盖。新增功能应补充测试。

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
14. **🔴 Controller 禁止直接返回 Map/List（强制）** — Controller 层所有返回值必须封装为强类型 Response DTO
    - **禁止** `Result<Map<String, Object>>`、`Result<Map<String, Long>>` 等 — 必须封装为 Response DTO
    - **禁止** `Result<List<XxxResponse>>`、`Result<List<String>>` 等 — 必须封装为 `XxxListResponse` 对象（含 `records` 属性）
    - Response DTO 放在 **application 层** `dto/` 包中（`application/.../dto/XxxResponse.java`）
    - 若 ApplicationService 方法返回裸 Map/List，**一并改为返回 Response DTO**，从源头杜绝
    - DTO 使用 `@Data` + `@Builder` + `@NoArgsConstructor` + `@AllArgsConstructor` + `@Schema`
    - 这确保：编译期类型安全、Swagger 自动生成字段文档、后续扩展字段无需破坏性变更
15. **🔴 业务处理层 catch 异常后必须向上 re-throw，禁止吞掉异常（强制）**
    - 应用层 Service / DomainService 的 catch 块，完成日志记录 + 状态更新后**必须重新抛出**
    - 正确模式: `catch (Exception e) { log.error("...", e); throw new BusinessException(500, "...", e); }`
    - **禁止** `catch (Exception e) { log.error("...", e); }` 后无声返回 — 这会隐藏真实故障，导致上层状态不一致
    - **例外**（可 fail-open，但必须 `log.warn` 或更高级别）: 审计日志写入、WebSocket 推送、可观测性埋点上报、定时任务逐条扫描（单条失败不影响后续）
16. **🔴 Spring 6.x `Trigger.nextExecution()` 返回 `Instant` 非 `Date`** — 动态调度时 `TriggerContext.lastScheduledExecutionTime()` 返回的类型需用 `Object` 接收以兼容不同 Spring 版本，最终统一用 `Instant.ofEpochMilli()` 返回
    - 业务参数校验使用 `BizAssert` 工具类替代散落的 `if-check + throw`:
      ```java
      BizAssert.notNull(user, 404, ExceptionMessages.USER_NOT_FOUND + userId);
      BizAssert.isReached(DocumentStatus.PARSED, doc.getStatus(), 400, "文档必须先解析完成才能切片");
      BizAssert.hasText(name, 400, ExceptionMessages.VALIDATION_ERROR + "名称不能为空");
      ```
    - `BizAssert` 位于 `common/exception/BizAssert.java`，对标 `org.springframework.util.Assert`
17. **🔴 project-memory 文件必须用递增序号命名（强制）** — 新建快照/总结文件时，查看 `docs/project-memory/` 中已有文件的最大序号，使用 `{max+1}-文件名.md` 格式
    - **禁止**使用 `XX-` 或 `TODO-` 等占位前缀
    - 文件命名示例: `35-配置治理-子方案05-Sentinel规则Nacos持久化.md`、`36-会话总结-2026-07-14-子方案05.md`
    - 同步更新 `docs/project-memory/README.md` 时，链接路径必须与实际文件名一致

---

## 当前 Java 代码（573 个文件，P0 + P1 + P2 + P3 + P4 + P6 + P7 + 配置治理子方案 01-04 核心已实现）

```
agent-platform-bootstrap/     1 文件  ← @SpringBootApplication + @EnableAsync
agent-platform-common/       27 文件  ← Result、6 异常、BizAssert、PageResponse、IdGenerator、安全异常、值对象基类、🆕 ProjectConstants（7内部类25常量）
agent-platform-domain/      125 文件  ← 23 聚合根/实体 + 23 仓储接口 + 32 值对象 + 5 安全接口 + 19 DomainService/端口 + 3 交互策略
agent-platform-application/ 157 文件  ← 19 AppService + 3 识别器 + 1 责任链 + 5 提取器 + 4 Resolver + 5 Handler + 7 切片策略 + 1 管线 + Security DTO + Event + 4 交互策略
agent-platform-infrastructure/ 138 文件 ← 23 PO + 23 Mapper + 23 Impl + ServiceImpl + Config + Rag + Observability + AgentMetrics + McpClientManager + HttpToolAdapter + Annotation + Aspect + 🆕 SchedulerConfig + DynamicScheduledTaskManager + 🆕 RagConfig + AiModelConfig + SecurityConfig + SessionConfig + 🆕 Sentinel Nacos 持久化 + ...
agent-platform-interfaces/  125 文件  ← 21 Controller + ~102 Request/Response DTO + ExceptionHandler + SwaggerConfig + 认证 DTO
```

> ✅ 已实现：多租户 RBAC、意图识别 3 层链、对话管理、SSE/WebSocket 流式、状态机、长期记忆、T4 提示词管理、T5 任务规划引擎、T6 RAG 知识库、T7 MCP 工具平台、T10 安全围栏、T11 人机协同审批、T9 全链路可观测性、T12 效果评估与持续优化、**P6 迭代增强（Redis缓存/Reranker/工具版本化/心跳检测/精度监控/LDAP/SSO/Presidio）、P7 多模式交互（策略工厂 + 2 种模式）、🆕 P6 配置治理子方案01（@Scheduled → Nacos）、🆕 子方案02（RagConfig 24 参数）、🆕 子方案03（AiModel/Security/Session 17 参数）、🆕 子方案04（ProjectConstants 静态常量统一管理、25常量、26文件改造）、🆕 子方案05（Sentinel 规则 Nacos 持久化、6 项规则）**
> 📐 DDD 架构：Controller → ApplicationService → DomainService → Repository，禁止越层调用
> 📦 DTO 分离：Application 层 DTO 独立分包 + Interfaces 层 Request DTO 独立分包
> 🔜 待完成：P5 前端；主链路接线（围栏/意图/记忆/工具/审批）见 `docs/已设计未实现清单.md`

---

## 数据库（29 张表 + V1.5.0，手动管理）

- **V1.0.0** (13张): t_tenant, t_user, t_role, t_permission, t_user_role, t_role_permission, t_agent_config, t_conversation, t_message, t_knowledge_base, t_tool_registry, t_prompt_template, t_evaluation_run
- **V1.1.0** (16张): t_intent, t_long_term_memory, t_prompt_template_version, t_task_execution, t_task_step_execution, t_document, t_document_chunk, t_knowledge_hit_record, t_tool_invocation_log, t_sensitive_word, t_security_event, t_audit_log, t_approval_workflow, t_evaluation_dataset, t_evaluation_dataset_item, t_optimization_ticket
- **V1.2.0**: 管理员种子数据 (admin/Mhqf@123456)
- **V1.2.1**: 业务 ID 字段补充（conversation/message/intent/long_term_memory 表）+ 权限种子数据（44 条权限码）
- **V1.2.2**: 🆕 T7 工具调用日志业务 ID（t_tool_invocation_log.invocation_id）
- **V1.3.0**: T6-RAG: t_knowledge_base 14 个精度控制字段
- **V1.4.0**: 🆕 KB 文件管理升级: created_by + 状态迁移 + chunk.deleted
- **V1.5.0**: 🆕 T7 工具版本化: t_tool_registry 版本字段
- 所有 SQL 位于 `docs/database/`，共 9 个迁移文件

---

## 开发优先级

```
P0(收尾✅) → P1(T3-T5✅) → P2(T6-T7✅) → P3(安全✅) → P4(观测✅核心) → P6(增强✅) → P7(多模式✅) → P5(前端⬜)
统一网关✅     意图识别✅      RAG引擎✅     安全围栏✅    全链路✅核心   观测增强✅    策略工厂✅     交互端⬜
多租户✅       提示词管理✅    MCP平台✅     人机协同✅    效果评估✅     配置治理🟡   2种模式✅
              任务规划✅                                        运维部署🔜
```

**P5**: 前端交互层（Web聊天/审批卡片/反馈/IM），与后端并行开发
**P6**: 迭代优化（9 项代码增强已实现：Reranker/工具版本化/Redis缓存/心跳检测/精度监控/LDAP/SSO/Presidio），🆕 配置治理（子方案01✅ 02✅ 03✅ 04✅ 05已设计），11 项运维设施待部署

### ⚠️ 已知差距速览

| 类别 | 数量 | 说明 |
|------|:--:|------|
| 代码功能缺口 | 2 项 | P5 前端交互层、反馈聚合统计 API（P6 已补 9 项代码缺口） |
| 运维设施缺口 | 11 项 | Grafana/OTel/ELK/AlertManager/Docker/K8s/MinIO/Presidio/LDAP 等 |
| 方案变更 | 5 项 | 均为「简化设计」方向，详见 `docs/开发进度.md` |
| 测试覆盖 | ⚠️ 极低 | 仅 1 个 Spring Boot 上下文测试，无业务逻辑单元测试 |

---

## 关键文档索引

| 文档 | 路径 |
|------|------|
| 项目现状摘要 | `docs/project-memory/00-项目现状摘要.md` |
| 会话索引 | `docs/project-memory/README.md` |
| 开发进度 | `docs/开发进度.md` |
| 已设计未实现清单 | `docs/已设计未实现清单.md` |
| 未实现事项分项方案 | `docs/未实现事项设计方案/README.md` |
| P0-P4 差距分析（历史） | `docs/project-memory/22-P0-P4-差距分析-2026-06-18.md` |
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

MySQL(`:3306`) + Redis(`:6379`) + Milvus(`:19530`) + MinIO(可选，文件上传)

## 应用入口

- 启动类: `agent-platform-bootstrap/.../AgentPlatformApplication.java`（`@EnableAsync`）
- 默认端口: `8080`
- Swagger: `http://localhost:8080/swagger-ui.html` — Sa-Token Bearer 鉴权
- 健康检查: `http://localhost:8080/actuator/health`
- Prometheus 指标: `http://localhost:8080/actuator/prometheus`
