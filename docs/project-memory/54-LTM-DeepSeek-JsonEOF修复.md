# LTM DeepSeek JsonEOF 修复

> **日期**: 2026-11-08 17:15
> **分支**: master
> **触发**: Bug修复

## 做了什么
- 定位长期记忆异步提取 `LongTermMemoryService.summarizeConversation` 经 Spring AI `.call()` 反序列化 DeepSeek 响应时出现 `JsonEOFException`
- 新增 `DeepSeekUtilityClient`：直连 `/chat/completions`，强制 `thinking.type=disabled`，设置读写超时
- LTM 改为走工具型客户端；LLM 瞬时失败降级为 WARN fail-open
- `AiConfig` 默认选项从错误的 `OpenAiChatOptions` 改为 `DeepSeekChatOptions`
- `MemoryType.valueOf` → `fromCode`

## 关键决策
- 根因：`deepseek-v4-pro` 默认开启 thinking；Spring AI 1.1.7 无 thinking 开关；非流式大响应易截断，而主对话流式链路不受影响
- 不升级 Spring AI（与 Boot 3.3.7 锁定）；工具型任务旁路 ChatClient，主对话仍用流式 ChatClient

## 踩坑记录
- 同机 curl 带 `thinking.disabled` 可稳定返回；默认 thinking 也能返回，但 Spring AI 非流式路径不稳定
- `AiConfig` 长期误用 `OpenAiChatOptions` 包装 DeepSeek ChatClient

## 下一步
- 重启后端后验证对话结束不再刷 `[LTM] 提取失败` / `JsonEOFException`
- 其它后台 `.call()`（意图/规划/评估）若再现同类问题，可复用 `DeepSeekUtilityClient`
