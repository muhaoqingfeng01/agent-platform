# 隐藏 UNKNOWN → CONVERSATION 意图提示

> **日期**: 2026-11-08 17:25
> **分支**: master
> **触发**: Bug修复

## 做了什么
- 后端：`StreamOrchestrationService` 仅在 `intent.isMatched()` 时推送 SSE `intent` 事件
- 前端：`EventBlocks` 对 `UNKNOWN` 不渲染；有意义建议模式才显示「→ 建议xxx」

## 关键决策
- `UNKNOWN → CONVERSATION` 是未匹配时的默认建议，对用户无信息量，属于调试噪音
- 库表 `t_intent` 无种子意图时会长期 UNKNOWN，属配置缺口而非前端故障

## 下一步
- 若需真实意图识别，在管理端/SQL 配置租户活跃意图
