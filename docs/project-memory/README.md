# 项目会话记忆索引

> 🚀 **下次会话优先加载本目录下所有文件**
>
> 加载顺序: `00-项目现状摘要.md` → 各会话文件按时间倒序

## 会话列表

| 序号 | 日期 | 会话文件 | 核心内容 |
|:--:|------|----------|----------|
| 1 | 2026-06-10/11 | [01-项目初始化与修复.md](01-项目初始化与修复.md) | JDK 降级、依赖修复、项目清理 |
| 2 | 2026-06-11 | [02-数据库设计.md](02-数据库设计.md) | 28 张表设计、Flyway 迁移 |
| 3 | 2026-06-11 | [03-Swagger集成.md](03-Swagger集成.md) | SpringDoc OpenAPI 配置 |
| 5 | 2026-06-13 | [05-对话自动快照系统.md](05-对话自动快照系统.md) | Stop Hook + settings.json + CLAUDE.md 指令 |

| 6 | 2026-06-14 | [07-日志链路追踪与数据库仓储实战化.md](07-日志链路追踪与数据库仓储实战化.md) | TraceFilter(traceId+spanId)、Tenant/Role/Permission 仓储 MyBatis 实战化 |

## 当前项目状态快照

- **JDK**: 17
- **Spring Boot**: 3.3.7
- **构建**: ✅ BUILD SUCCESS（7/7 模块）
- **启动**: ⚠️ 需要 MySQL + Redis + Milvus 外部服务
- **代码**: 30+ 个 Java 文件（含 Controller/Service/Repository/PO/Mapper/Filter）
- **Swagger**: 已集成，`/swagger-ui.html`
- **数据库**: 13 张表 V1.0.0 + 15 张表 V1.1.0 + 种子数据 V1.2.0
- **日志**: traceId + spanId 全链路追踪（MDC）
