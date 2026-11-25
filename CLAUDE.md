# CLAUDE.md — Agent Platform 项目记忆

> 此文件由 Claude Code 在每次会话启动时自动加载。
> **文档总入口**: `docs/README.md`（00 总体设计 / 01 模块设计 / 02 任务事项 / 03 实现进度）
> `docs/` **只保留设计与进度**；不写会话流水账。

---

## 🤖 文档同步规则（每次会话必须遵守）

**核心原则: 技术方案改 `docs/00`～`02`；进度改 `docs/03-实现进度/`。不要再建会话记忆目录。**

### 🔴 代码完成后自动同步（强制）

**每次代码开发任务完成后，无需等待用户提醒，自动执行以下同步：**

| # | 同步文件 | 更新内容 |
|:--:|------|------|
| 1 | `docs/03-实现进度/进度总览.md` | 模块与任务事项状态（优先） |
| 2 | `docs/03-实现进度/开发进度.md` | 功能点级详细打卡（可选） |
| 3 | `docs/02-任务事项/待办事项` → `已办事项` | 完成则**移动**方案文件，并更新两边 README |
| 4 | `CLAUDE.md` | 当前阶段、优先级图 ✅ 标记、待完成列表 |

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

> 📋 详细规范：`docs/00-总体设计/07-开发规范.md`

---

## 项目定位

**企业级 AI Agent 平台** — DDD 六模块 Maven 多模块项目，P0-P4 核心已全部实现，P5 Web 聊天（混合风壳 + 停生成/重生）/审批卡片/反馈/IM 回调已落地（`agent-platform-web/`），主链路围栏/提示词/记忆/意图/审批/精度已接线，P6 配置治理 01-05 已实现，运维编排文件在 `deploy/`（环境待实际拉起）。

- **路径**: `D:\mhqf_project\heavenly-craft-agent\agent-platform`
- **包名**: `com.example.agent`
- **模块**: common → domain → application → infrastructure → interfaces → bootstrap

---

## 环境速查

| 组件 | 版本 | 备注 |
|------|------|------|
| JDK | **17.0.18** | `C:\tools\Java\jdk-17.0.18` |
| Spring Boot | **3.3.7** | 不要升级到 3.5.x |
| Spring Cloud Alibaba | 2023.0.3.2 | Boot 3.3.x 线 |
| Nacos Client | **3.3.0** | 覆盖 SCA 默认 2.4.2，对齐 Server 3.3.0 |
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
17. **🔴 不要在 `docs/` 下新建会话记忆目录** — 设计进 `00/01/02`，进度进 `03`；禁止再写 `project-memory` / `archive` 流水账

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
> 🔜 待完成：SSO/OIDC、ColBERT、管理后台二期（提示词/工具/任务/评测）、生产环境实际部署 Grafana/OTel/K8s

---

## 数据库（全量脚本维护，手动执行）

> **唯一维护入口**: [`docs/database/agent_platform_full.sql`](docs/database/agent_platform_full.sql)  
> 后续 DDL/DML **追加到文件末尾**，并在文件头变更记录表写明时间与作用。历史 `V1.x.x__*.sql` 仅归档。

## 数据库（历史分版本说明，29 张表 + V1.5.0+）

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
P0(收尾✅) → P1(T3-T5✅) → P2(T6-T7✅) → P3(安全✅) → P4(观测✅核心) → P6(增强✅) → P7(多模式✅) → P5(聊天✅/其余⬜)
统一网关✅     意图识别✅      RAG引擎✅     安全围栏✅    全链路✅核心   观测增强✅    策略工厂✅     Web聊天✅
多租户✅       提示词管理✅    MCP平台✅     人机协同✅    效果评估✅     配置治理🟡   2种模式✅     审批/反馈/IM⬜
              任务规划✅                                        运维部署🔜
```

**P5**: Web 聊天 + 审批/反馈/IM + 管理后台一期 ✅；见 `docs/03-实现进度/前后端对齐完整度.md`  
**P6**: 配置治理 01–05 ✅；运维 D 项待部署。见 `docs/03-实现进度/进度总览.md`

### ⚠️ 已知差距速览

| 类别 | 说明 |
|------|------|
| 待做任务 | A4/A5/A6、B7、C2/C4/C6、D 运维、E1/E2 — 见 `docs/02-任务事项/` |
| 方案变更 | 5 项简化设计，详见 `docs/03-实现进度/开发进度.md` |
| 测试覆盖 | 极低；见任务 E1 |

---

## 关键文档索引

| 文档 | 路径 |
|------|------|
| **文档总入口** | `docs/README.md` |
| 总体设计 | `docs/00-总体设计/` |
| 模块子设计 P0–P7 | `docs/01-模块设计/` |
| 待办 / 已办事项 | `docs/02-任务事项/待办事项/` · `已办事项/` |
| 实现进度总览 | `docs/03-实现进度/进度总览.md` |
| 前后端对齐 | `docs/03-实现进度/前后端对齐完整度.md` |
| 开发规范 | `docs/00-总体设计/07-开发规范.md` |
| 数据库 / SQL | `docs/database/` |
| Swagger 配置 | `agent-platform-interfaces/.../config/OpenApiConfig.java` |

---

## 启动依赖的外部服务

MySQL(`:3306`) + Redis(`:6379`) + Milvus(`:19530`) + MinIO(可选，文件上传)

## 应用入口

- 启动类: `agent-platform-bootstrap/.../AgentPlatformApplication.java`（`@EnableAsync`）
- 默认端口: `8080`
- Swagger: `http://localhost:8080/swagger-ui.html` — Sa-Token Bearer 鉴权
- 健康检查: `http://localhost:8080/actuator/health`
- Prometheus 指标: `http://localhost:8080/actuator/prometheus`
