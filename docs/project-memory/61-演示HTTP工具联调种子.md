# 演示 HTTP 工具联调种子

> **日期**: 2026-11-08 22:30
> **分支**: master
> **触发**: 代码生成

## 做了什么
- 新增 `DemoHttpToolController`：`/api/v1/demo-tools` 下 4 个 POST 端点（echo / current-time / city-weather / calculator）
- Sa-Token 白名单放行 `/api/v1/demo-tools/**`，便于后端 `HttpToolAdapter` 本机回环调用
- `HttpToolAdapter` 增强：`{param}` 路径替换；GET/DELETE 追加 query
- SQL 种子 V1.9.0：租户 `1000001` 注册 4 个 ACTIVE HTTP 工具
- `scripts/seed-demo-tools.http`：API 注册与直连探测脚本

## 关键决策
- 优先 HTTP 演示工具，不伪造 MCP：真实 MCP 需独立 SSE Server，本地联调成本高
- endpoint 写死 `127.0.0.1:8081`（与 `application.yml` 默认端口一致）
- schema 内带 `"method":"POST"` 以兼容现有 `resolveHttpMethod`

## 踩坑记录
- 自调用若走 `/api/v1/**` 且未白名单，会因无 Token 被 Sa-Token 拦截
- GET 工具此前不拼 query，外部 GET API 联调会失败（已补）

## 下一步
- 执行 SQL 追加段或跑 `.http` 注册后，在聊天中问「现在几点」「上海天气」「算一下 12*3.5」验证工具桥
- 若需 MCP：再接真实 MCP SSE（如 filesystem/fetch）并在 `t_tool_registry` 注册 `tool_type=MCP`
