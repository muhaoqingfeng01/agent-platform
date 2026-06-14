# 企业级 Agent 平台技术方案汇总

## P5 交互端构建（Web/IM 聊天、审批卡片、反馈）

### 使用技术
- React (Next.js / Vite)
- Ant Design
- WebSocket
- react-window（虚拟滚动）
- 企业微信/钉钉消息 API（适配层）

### 实现思路
1. **Web 聊天界面**：基于 React 构建 SPA，消息列表使用 `react-window` 实现虚拟滚动，保证长会话流畅度。使用 WebSocket 与服务端建立长连接，消息格式统一为 JSON，按 `type` 区分 `message`、`approval`、`feedback` 等类型。
2. **审批卡片**：定义自定义消息类型 `approval`，前端渲染为包含业务详情和“同意/拒绝”按钮的卡片组件。点击按钮后通过 WebSocket 将审批结果回传，附带审批 ID 和操作。
3. **用户反馈**：每条助手消息下方预设点赞/点踩图标，点击后发送 `feedback` 类型消息，携带 `messageId` 和 `rating`（1/-1），最终落入审计存储。
4. **IM 接入**：构建统一消息适配器层，将企微/钉钉的消息格式转化为内部统一结构，回调 URL 对接网关服务，实现跨渠道统一体验。

---

## P0-T2 统一网关与安全流控

### 使用技术
- Spring Cloud Gateway
- Sa-Token
- Sentinel
- Sa-Token
- LDAP / SSO (OAuth2/OIDC)

### 实现思路
1. 使用 Spring Cloud Gateway 作为 API 入口，路由规则分发至不同微服务。
2. 全局 Filter 中集成 Sa-Token 鉴权，对 `/api/**` 进行 Token 校验，并支持对接企业 LDAP 或 SSO 实现统一认证。
3. 集成 Sentinel 进行流量控制，按租户+API 维度设置限流规则（如每租户每秒最大 50 请求），配置熔断策略（错误率>50% 时熔断 30 秒）。
4. 网关层统一处理 CORS、请求日志和响应日志，为可观测性提供基础数据。

---

## P1-T3 意图识别与对话管理

### 使用技术
- Spring AI
- Spring StateMachine
- Redis
- MySQL

### 实现思路
1. **意图识别**：采用“规则优先+LLM 兜底”策略。正则/关键词匹配覆盖 80% 高频意图，未命中则调用 LLM 进行意图分类，返回预定义的意图枚举。
2. **对话状态管理**：使用 Spring StateMachine 定义对话流程状态（`INIT`、`AWAIT_PARAM`、`EXECUTING`、`CONFIRMATION`），根据当前意图和参数完整性触发状态转换。
3. **记忆管理**：
    - 短期记忆：Redis 中存储最近 N 轮对话记录，key 格式 `session:{tenantId}:{sessionId}`，设置 TTL 30 分钟。
    - 长期记忆：用户画像和重要事实异步写入 MySQL，供后续会话加载。

---

## P1-T4 提示词管理与版本控制

### 使用技术
- MySQL
- Redis
- 自定义管理后台（React/Spring Boot）

### 实现思路
1. 数据库设计 `prompt_templates` 表，字段包括 `id`、`name`、`version`、`template_content`（含占位符）、`variables`（JSON 数组）、`status`、`created_at`。
2. 运营后台提供模板在线编辑、测试（可输入变量值预览渲染结果）和发布功能，发布后版本号自增，旧版本保留用于回滚。
3. 编排引擎通过 `PromptService.getTemplate(name, version)` 获取模板，结合上下文变量（如用户信息、检索到的知识库片段）渲染最终 system prompt。热点模板使用 Redis 缓存以提高性能。

---

## P1-T5 任务规划与执行引擎

### 使用技术
- Spring AI（任务规划调用 LLM）
- 自研轻量 DAG 执行器（Java 并发框架）
- Redis（任务状态缓存）

### 实现思路
1. 对于复杂意图，发送专用提示词给 LLM，要求返回 JSON 格式的任务序列和依赖关系，如 `[{"id":"1","action":"retrieve_order","dep":[]},...]`。
2. 实现 DAG 解析器，检查无依赖节点并行执行，每个节点对应一个 `ActionHandler` 接口实现（如 `RetrieveOrderHandler`）。
3. 执行器监控节点状态，支持失败重试（最多 3 次）、超时熔断。执行过程中状态同步至 Redis，并通过 WebSocket 实时推送给前端更新进度。

---

## P2-T6 RAG 知识库引擎

### 使用技术
- Apache Tika（文档解析）
- Milvus 2.6.9（向量存储）
- MySQL（元数据）
- Elasticsearch（可选，全文检索）
- MinIO（文档存储）
- text-embedding-3-small 或类似向量模型

### 实现思路
1. **文档入库**：用户上传文件至 MinIO，触发异步解析流程，使用 Tika 提取纯文本，按语义/段落切分为 chunk（大小 512 token，重叠 50 token），保留源数据（文件名、页码等）。
2. **向量化与存储**：调用 Embedding 模型为每个 chunk 生成向量，写入 Milvus 的 Collection `kb_{tenantId}`，同时将 chunk 元数据写入 MySQL。
3. **混合检索**：查询时同时进行 Milvus 向量检索（Top K）和 MySQL 全文索引（或 Elasticsearch）的 BM25 检索，通过 RRF 算法融合排序，取 Top N 作为上下文。
4. **知识运营**：提供后台查看命中记录，支持人工标注“优秀/需纠正/补全”，反馈数据用于持续优化知识库质量。

---

## P2-T7 MCP 工具平台

### 使用技术
- Spring AI MCP（客户端）
- Nacos（服务注册与发现）
- 适配器模式（HTTP -> MCP）

### 实现思路
1. 各业务系统（OA、ERP）对外暴露 MCP Server，将工具列表、描述和输入 schema 注册到 Nacos。
2. 核心编排服务内置 MCP Client，启动时从 Nacos 拉取工具清单并缓存。调用工具时携带租户上下文和安全凭证。
3. 对于无法提供标准 MCP 的遗留系统，运营端通过适配器模式手动录入 HTTP 接口，包装为 MCP 工具格式，实现统一调用。
4. 高风险工具标记 `require_approval=true`，调用时挂起并触发审批流程（见 P3-T11）。

---

## P0-T8 多租户数据与存储

### 使用技术
- MySQL（字段隔离或动态数据源）
- Milvus（Collection 级隔离）
- Redis（Key 前缀隔离）
- Spring 拦截器/过滤器（透明注入 tenant_id）

### 实现思路
1. **MySQL 隔离**：业务表统一添加 `tenant_id` 字段，通过拦截器从 Token 中提取租户 ID 并注入到 SQL 查询条件，确保单库共享但数据逻辑隔离。
2. **Milvus 隔离**：每个租户创建独立 Collection，命名规则 `kb_{tenantId}`，在请求时根据租户 ID 动态选择 Collection。
3. **Redis 隔离**：所有缓存 Key 强制使用 `tenant:{tenantId}:{module}:{id}` 格式，从源头避免数据混淆。
4. 预留动态数据源能力，若未来需要严格物理隔离，可通过路由切换至独立数据库实例。

---

## P4-T9 全链路可观测性

### 使用技术
- Langfuse（LLM 追踪与评估）
- Spring Micrometer + Prometheus + Grafana（指标监控）
- Elasticsearch + Logstash/Kibana（日志审计）
- OpenTelemetry（分布式追踪，可选）

### 实现思路
1. **链路追踪**：通过 Spring AI 集成 Langfuse，自动记录 LLM 调用、RAG 检索、工具调用的耗时、Token 消耗和输入输出。每次对话生成唯一 `traceId`，串联全流程。
2. **指标监控**：业务指标（QPS、延迟、错误率）通过 Micrometer 暴露给 Prometheus 抓取，Grafana 展示仪表盘并配置告警（如错误率超阈值、QPS 突降）。
3. **日志审计**：所有用户输入、模型输出、工具参数写入 Elasticsearch，设置索引生命周期，保留 90 天以上，支持按租户、时间、关键词查询。

---

## P3-T10 安全围栏

### 使用技术
- 自定义拦截器（责任链模式）
- 正则表达式 / NER 模型（PII 识别）
- Microsoft Presidio（可选，增强脱敏能力）

### 实现思路
1. **输入过滤**：在网关或编排入口，基于规则和敏感词库过滤指令注入、越狱提示词等危险输入。
2. **输出脱敏**：在响应返回前，识别输出内容中的身份证、手机号、邮箱等 PII，使用正则或 Presidio 识别并替换为 `***`。
3. 采用责任链模式：`InputFilters -> Orchestration -> OutputFilters`，任一拦截器可抛出合规异常阻断请求，并记录告警。
4. 安全事件全部写入审计日志，支持追溯和合规审查。

---

## P3-T11 人机协同审批

### 使用技术
- 自研轻量级审批工作流引擎
- WebSocket（推送审批卡片）
- 数据库表（审批记录）

### 实现思路
1. 工具注册时标记风险等级，高风险工具调用时生成审批工单，记录操作内容、参数、请求人，状态设置为“待审批”。
2. 通过 WebSocket 将审批卡片推送给拥有审批权限的用户（或管理员）。审批者点击同意/拒绝后，系统更新工单状态并通过 WebSocket 回调引擎。
3. 引擎根据审批结果继续执行或终止任务，并向请求人推送最终结果。
4. 设置审批超时时间（如 5 分钟），超时后自动按预设策略处理（拒绝或升级）。

---

## P4-T12 效果评估与持续优化

### 使用技术
- Langfuse（评分与评估数据集）
- 数据管道（采集在线反馈）
- 自定义评估脚本 / LLM-as-Judge

### 实现思路
1. **在线评估**：聚合用户反馈（点赞/点踩）、会话时长、追问次数等指标，加权计算会话质量分，展示在 Dashboard 上。
2. **离线评估**：从生产日志中采样构建评估数据集（问题、标准答案、检索上下文），使用 LLM as Judge 或人工评估方式计算准确率、幻觉率、检索命中率。
3. **BadCase 闭环**：自动将点踩会话转化为优化工单，指派运营分析，触发知识补全、提示词调整或模型微调数据积累。
4. 建立周/月度评估报告，驱动平台能力持续提升。