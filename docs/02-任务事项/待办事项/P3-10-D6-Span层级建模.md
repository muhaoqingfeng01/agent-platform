# D6 Span 层级建模实现方案

> **类别**: D | **优先级**: P3  
> **依赖**: D2 OTel

## 1. 现状

单 spanId，无 LLM/RAG/Tool 子 span 语义。

## 2. 设计要点

约定 span 名与属性（属性勿含原文 PII）：

| span | 属性 |
|------|------|
| `http.server` | route |
| `agent.pipeline` | mode |
| `agent.intent` | intentCode, layer |
| `agent.rag.search` | kbId, topK |
| `llm.chat` | model, tokens |
| `tool.invoke` | toolId, status |

与 Langfuse 并存：Langfuse 管生成内容，OTel 管拓扑。

## 3. 实现

在 B8 管道各中间件 `startSpan/end`。采样与 D2 相同。

## 4. 验收

一次知识检索 trace 含 rag + llm 子 span。

## 5. 风险

span 过多，工具循环要限制。
