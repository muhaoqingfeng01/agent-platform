# B3 长期记忆注入 Prompt 实现方案

> **类别**: B | **优先级**: P0  
> **现有代码**: `LongTermMemoryService.loadUserMemories` / `injectMemoriesIntoPrompt`；stream 只在完成后 `extractAndSave`

## 1. 现状与缺口

记忆只写不读，多轮偏好无法影响回答。

## 2. 解决什么场景

用户说过「不要用英文」「我负责华东区」，后续对话自动带上。

## 3. 为什么这样设计

- 读路径必须 **限制条数与 token**，防止撑爆上下文。按 `MemoryType` 优先级（已有）取 Top。  
- 注入 system/前缀，不覆盖用户当前句。  
- `TenantContext` 在 stream 线程已设置，`loadUserMemories` 可用。

## 4. 技术栈

现有 LTM 服务；与 B4 组合时记忆作为模板变量 `{{long_term_memory}}`。

## 5. 实现方案

1. `StreamOrchestrationService` 在 `buildFullPrompt` 前：`memories = ltm.loadUserMemories(userId)`。  
2. 截断：按 SessionConfig 增加 `ltmPromptMaxChars`（默认 1500）。  
3. `injectMemoriesIntoPrompt(basePrompt, memories)`。  
4. 无记忆则跳过。  
5. 知识检索管线同样注入（避免两套人格）。

## 6. 流程图

```mermaid
flowchart LR
    U["userId"] --> L["loadUserMemories"]
    L --> CUT["截断"]
    CUT --> P["injectMemoriesIntoPrompt"]
    P --> LLM
```

## 7. 验收标准

- 库中有 PREFERENCE 时，Prompt 含该文本（单测 mock repository）。  
- 超长被截断且日志 warn。

## 8. 风险

过期记忆：`loadUserMemories` 已滤 expireAt，保持即可。异步抽取失败已 swallow，不影响主路径。
