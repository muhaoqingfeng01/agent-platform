# 移除 Flyway 数据库迁移框架

> **日期**: 2026-06-18
> **分支**: master
> **触发**: 架构决策

## 做了什么
- 从 `agent-platform-bootstrap/pom.xml` 移除 `flyway-core` 和 `flyway-mysql` 依赖
- 从 `application-dev.yml` 移除 `spring.flyway.*` 配置块
- 从 `logback-spring.xml` 移除 `org.flywaydb` logger 配置
- 将 8 个 SQL 迁移文件 + 数据库设计文档从 `db/migration/` 移至 `docs/database/`
- 清理 `src/main/resources/db/` 空目录和 `target/classes/db/migration/`
- 更新 CLAUDE.md、开发进度.md、项目现状摘要，将所有 Flyway 引用替换为"手动管理/文档参考"

## 关键决策
- **移除原因**: Flyway 仅在启动时执行一次迁移，与业务逻辑零耦合，移除不影响运行时
- **替代方案**: SQL 文件保留在 `docs/database/` 作为参考，新环境部署时手动按版本顺序执行
- **零 Java 代码改动**: Flyway 完全由 Spring Boot 自动配置驱动，无任何 Java 代码直接调用

## 踩坑记录
- 无踩坑，改动影响范围极小（2 个配置文件 + 8 个 SQL 文件归档）

## 变更文件清单
| 文件 | 操作 |
|------|------|
| `agent-platform-bootstrap/pom.xml` | 删除 flyway-core + flyway-mysql 依赖 |
| `application-dev.yml` | 删除 spring.flyway.* 配置 |
| `logback-spring.xml` | 删除 org.flywaydb logger ×2 |
| `db/migration/*.sql` (8 个) | 移至 `docs/database/` |
| `db/migration/数据库设计文档.md` | 移至 `docs/database/` |
| `CLAUDE.md` | Flyway→手动管理 |
| `docs/开发进度.md` | 6 处 Flyway→SQL 迁移参考 |
| `docs/project-memory/00-项目现状摘要.md` | 4 处 Flyway→手动管理 |
