# 配置治理 — 子方案05：Sentinel 规则 Nacos 持久化

> **日期**: 2026-07-14 12:48
> **分支**: nacos/nacos-config
> **触发**: 代码生成

## 做了什么

1. **新增依赖**: `sentinel-datasource-nacos` 添加到 `infrastructure/pom.xml`（非传递依赖，需显式声明）
2. **改造 application.yml**: 通用占位 `datasource.nacos.*` → 3 个命名 DataSource（ds-flow/ds-degrade/ds-system）
3. **改造 SentinelConfig.java**: 用 `@ConditionalOnExpression("${app.sentinel.fallback.enabled:false}")` 条件装配 + 加载前判空防覆盖 Nacos 规则
4. **Nacos 配置模板**: 流控/熔断规则 JSON（需在 Nacos 控制台手动创建）

## 关键决策

1. **硬编码 fallback 策略**: `@ConditionalOnExpression` 默认 false → 正常模式 Nacos 加载规则，仅当显式 `app.sentinel.fallback.enabled=true` 时启用硬编码兜底
2. **防规则覆盖**: `initFallbackRules()` 加载前检查 `FlowRuleManager.getRules().isEmpty()` — Nacos DataSource 已加载规则则跳过
3. **Spring 生命周期天然优先级**: `@PostConstruct` → 加载 fallback → `afterSingletonsInstantiated()` → Nacos DataSource 覆盖 → Nacos 规则最终生效
4. **Group 隔离**: Sentinel 规则使用 `SENTINEL_GROUP`，与业务配置 `AGENT-PLATFORM-CONFIG_ENTITY` 隔离

## 踩坑记录

- `sentinel-datasource-nacos` 不在 Spring Cloud Alibaba 2023.0.3.2 的传递依赖中 → 必须显式添加
- 原有 YAML `datasource.nacos.*` 是无效占位（缺少 ds-* 命名 + rule-type）→ Spring Boot 不会创建任何 DataSource
- `@ConditionalOnExpression` 中 `${}` 占位符由 PropertyResolver 先解析，然后 SpEL 求值 → 语法正确

## 下一步

- Nacos 控制台创建 `agent-platform-flow-rules.json` + `agent-platform-degrade-rules.json`（Group: SENTINEL_GROUP）
- 部署 Sentinel Dashboard → 通过 Dashboard 修改规则验证持久化
- 配置治理全部子方案 01-05 已完成 ✅
