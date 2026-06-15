# 项目会话记忆索引

> 🚀 **下次会话优先加载本目录下所有文件**
>
> 加载顺序: `00-项目现状摘要.md` → 各会话文件按时间倒序
>
> 📁 **会话快照**: `sessions/` 目录，按日期子目录分类存储
>    - `sessions/.session-log.jsonl` — 会话追加日志
>    - `sessions/YYYY-MM-DD/` — 按天分类的详细快照 JSON

## 会话列表

| 序号 | 日期 | 会话文件 | 核心内容 |
|:--:|------|----------|----------|
| 1 | 2026-06-10/11 | [01-项目初始化与修复.md](01-项目初始化与修复.md) | JDK 降级、依赖修复、项目清理 |
| 2 | 2026-06-11 | [02-数据库设计.md](02-数据库设计.md) | 28 张表设计、Flyway 迁移 |
| 3 | 2026-06-11 | [03-Swagger集成.md](03-Swagger集成.md) | SpringDoc OpenAPI 配置 |
| 4 | 2026-06-13 | [04-开发计划与技术方案.md](04-开发计划与技术方案.md) | P0-P5 技术方案拆分 |
| 5 | 2026-06-13 | [05-对话自动快照系统.md](05-对话自动快照系统.md) | Stop Hook + settings.json + CLAUDE.md 指令 |
| 6 | 2026-06-13 | [06-会话总结-2026-06-13.md](06-会话总结-2026-06-13.md) | 当日工作总结 |
| 7 | 2026-06-14 | [07-日志链路追踪与数据库仓储实战化.md](07-日志链路追踪与数据库仓储实战化.md) | TraceFilter、Tenant/Role/Permission 仓储 MyBatis 实战化 |
| 8 | 2026-06-14 | [08-日志配置与会话记录重组.md](08-日志配置与会话记录重组.md) | logback-spring.xml 按天分目录、sessions/ 目录重组、开发进度同步 |
| 9 | 2026-06-14 | [09-意图识别与对话管理.md](09-意图识别与对话管理.md) | P1-T3 核心实现：3 层意图识别链、对话管理、SSE/WebSocket 流式、状态机、长期记忆 |
| 10 | 2026-06-14 | [10-DDD架构重构与会话修复.md](10-DDD架构重构与会话修复.md) | ChatClient Bean 修复、DTO 抽取、DDD 分层强约束、用户权限领域建模 |
| 11 | 2026-06-14 | [11-DDD架构强制约束.md](11-DDD架构强制约束.md) | 🔴 强制规则：所有后续代码开发必须遵守 DDD 四层架构 |
| 12 | 2026-06-14 | [12-T4-提示词管理与版本控制实现.md](12-T4-提示词管理与版本控制实现.md) | P1-T4 提示词管理：CRUD + 版本发布/回滚 + 变量渲染链（20 个新文件） |
| 13 | 2026-06-14 | [12-T5任务规划执行引擎实现.md](12-T5任务规划执行引擎实现.md) | P1-T5 任务规划与执行：LLM 规划 + DAG 并行调度 + ActionHandler 注册与重试（31 个新文件） |
| 14 | 2025-06-15 | [13-T6-多策略切片方案设计.md](13-T6-多策略切片方案设计.md) | T6-RAG：6 种切片策略设计、手动选择+文件类型兜底、策略对比矩阵 |
| 15 | 2025-06-15 | [14-T6-向量检索精度控制方案.md](14-T6-向量检索精度控制方案.md) | T6-RAG：五层精度控制体系（索引选型+量化+检索参数+多阶段+监控闭环） |
| 16 | 2025-06-15 | [12-T6-RAG知识库引擎实现.md](12-T6-RAG知识库引擎实现.md) | T6-RAG 完整实现：~72 文件、DDD 四层、6 切片策略、混合检索、四级精度配置 |
| 17 | 2026-06-16 | [15-T6-知识库文件管理系统方案.md](15-T6-知识库文件管理系统方案.md) | V1.4.0 方案设计：KB 状态机、手动解析触发、Milvus 级联删除、指定 KB 检索、创建者权限 |

## 当前项目状态快照

- **JDK**: 17
- **Spring Boot**: 3.3.7
- **构建**: ✅ BUILD SUCCESS（7/7 模块）
- **启动**: ⚠️ 需要 MySQL + Redis + Milvus 外部服务
- **代码**: ~175 个 Java 文件（含 Controller/Service/DomainService/Repository/PO/Mapper/Filter/Recognizer/Extractor/Resolver/DTO）
- **Swagger**: 已集成，`/swagger-ui.html`
- **数据库**: V1.0.0(13张) + V1.1.0(15张) + V1.2.0(种子) + V1.2.1(业务ID) + V1.3.0(切片/精度) + **V1.4.0(KB创建者+状态迁移+chunk软删)**
- **日志**: traceId + spanId 全链路追踪（MDC），logback-spring.xml 按天分目录
- **LLM**: DeepSeek（`spring.ai.deepseek`），ChatClient Bean 已配置（`AiConfig.java`）
- **DDD 分层**: Controller → ApplicationService → DomainService → Repository，无越层调用
- **DTO 规范**: Request/Response 独立分包，Application 层 16 个 + Interfaces 层 13 个
- **会话记录**: `sessions/` — 按日期分目录管理
- **P0 进度**: 🟡 ~85%（待完成：ServiceImpl 切换、XML Mapper、单元测试）
- **P1-T3 进度**: 🟡 ~80%（DDD 分层重构完毕，待联调）
- **P1-T4 进度**: 🟢 ~100%（提示词管理 20 个文件已实现，编译通过）

## 会话记录目录结构

```
sessions/
├── .session-log.jsonl              ← 所有会话的追加日志（JSONL）
├── 2026-06-14/                     ← 按日期分目录
│   ├── session-2026-06-14T11-29-11+08-00.json
│   └── ...
└── 2026-06-15/
    └── ...
```

> 会话快照由 `.claude/hooks/session-snapshot.py` 在 Stop Hook 触发时自动生成，
> 仅在有文件变更时写入，无变更则静默跳过。
