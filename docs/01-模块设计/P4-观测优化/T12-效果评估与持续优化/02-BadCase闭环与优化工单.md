# BadCase 闭环与优化工单

> **状态**: ✅ 已实现 | **日期**: 2026-06-18 | **所属**: P4-T12

---

## 1. 设计目标

建立用户反馈驱动的 BadCase 闭环优化机制。

| 目标 | 说明 |
|------|------|
| **自动触发** | 用户点踩 → Spring Event → LLM 分析 → 自动创建工单 |
| **状态追踪** | 5 状态状态机，`TicketStatus.canTransitionTo()` 防非法流转 |
| **反馈统计** | 点赞率/问题分布/工单解决率 Dashboard |

---

## 2. 从点踩到工单的数据流

```
用户点踩
    │
    ▼
PATCH /api/v1/conversations/{id}/messages/{msgId}/feedback
    │
    ├── MessageRepository.updateFeedback(msgId, DISLIKE)  ← DB 更新
    │
    └── eventPublisher.publishEvent(MessageFeedbackEvent)  ← Spring Event
            │
            ▼
    ┌─────────────────────────────────────────┐
    │  BadCaseAutoTicketService                │
    │  @EventListener                          │
    │                                          │
    │  1. 过滤: feedback != DISLIKE → return   │
    │  2. 获取: 对话上下文（最近 5 条消息）     │
    │  3. LLM 分析: ChatClient.prompt()        │
    │     → 问题类型 / 严重程度 / 问题描述      │
    │  4. 创建: ticketId = "ticket_xxx"        │
    │     → OptimizationTicketRepository.save  │
    └─────────────────────────────────────────┘
```

### LLM 分析 Prompt 设计

```
分析以下 AI 对话中被用户点踩的回复，判断问题类型和严重程度。

对话上下文: {最近5条消息}
被点踩的消息ID: {msgId}

输出 JSON:
{
  "type": "HALLUCINATION|IRRELEVANT|INCOMPLETE|WRONG|OTHER",
  "severity": "LOW|MEDIUM|HIGH|CRITICAL",
  "description": "简要描述问题"
}
```

---

## 3. 工单状态机

```
                    ┌──────────────────────────┐
                    │                          │
                    ▼                          │
    ┌──────┐    ┌──────────┐    ┌──────────┐   │
    │ OPEN │───▶│ ANALYZING│───▶│IN_PROGRESS│   │
    └──┬───┘    └────┬─────┘    └─────┬────┘   │
       │             │                │        │
       │             │                ▼        │
       │             │          ┌──────────┐   │
       │             │          │ RESOLVED │───┘
       │             │          └────┬─────┘
       │             │               │
       ▼             ▼               ▼
    ┌──────────────────────────────────┐
    │             CLOSED (终态)         │
    └──────────────────────────────────┘
```

| 当前状态 | 可流转到 |
|---------|---------|
| OPEN | ANALYZING, IN_PROGRESS, CLOSED |
| ANALYZING | IN_PROGRESS, CLOSED |
| IN_PROGRESS | RESOLVED, CLOSED |
| RESOLVED | CLOSED |
| CLOSED | — (终态，不可变更) |

流转规则由 `TicketStatus.canTransitionTo()` 封装，Controller 层校验，非法流转直接抛异常。

---

## 4. 解决方案类型

| 类型 | 说明 | 后续动作 |
|------|------|----------|
| `KNOWLEDGE_FIX` | 知识库内容修正 | 更新文档 → 重新向量化 |
| `PROMPT_ADJUST` | 提示词调整 | 创建提示词新版本 |
| `MODEL_FINETUNE` | 积累微调数据 | 沉淀为微调数据集 |
| `OTHER` | 其他 | 人工处理备注 |

---

## 5. DDD 分层

```
interfaces/
  OptimizationTicketController   ← 工单 CRUD + 状态流转 + 反馈统计 (6 endpoints)
  MessageController (修改)        ← feedback 端点发布 MessageFeedbackEvent

application/
  OptimizationTicketService      ← 工单 CRUD + 状态机校验
  BadCaseAutoTicketService       ← @EventListener + LLM 分析
  event/MessageFeedbackEvent     ← Spring ApplicationEvent

domain/
  optimization/
    entity/OptimizationTicket
    repository/OptimizationTicketRepository
    valueobject/TicketStatus     ← 枚举 + canTransitionTo()

infrastructure/
  persistence/
    po/OptimizationTicketPO
    mapper/OptimizationTicketMapper
    impl/OptimizationTicketRepositoryImpl
  resources/mapper/
    OptimizationTicketMapper.xml
```

---

## 6. API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/optimization-tickets` | 工单列表 |
| GET | `/api/v1/optimization-tickets/{id}` | 工单详情 |
| PATCH | `/api/v1/optimization-tickets/{id}/assign` | 指派处理人 |
| PATCH | `/api/v1/optimization-tickets/{id}/status` | 更新状态（带校验） |
| POST | `/api/v1/optimization-tickets/{id}/resolve` | 提交解决方案 |
| GET | `/api/v1/optimization-tickets/feedback/stats` | 反馈统计面板 |

### 反馈统计响应

```json
{
  "totalTickets": 42,
  "resolvedTickets": 30,
  "resolveRate": "71.4%",
  "periodDays": 7
}
```

---

## 7. 事件驱动设计

```
MessageController.feedback()
        │
        │  eventPublisher.publishEvent()
        ▼
MessageFeedbackEvent (ApplicationEvent)
  ├── messageId
  ├── conversationId
  ├── tenantId
  └── feedback (LIKE / DISLIKE)
        │
        │  @EventListener
        ▼
BadCaseAutoTicketService.onMessageDisliked()
  ├── feedback != DISLIKE → return (点赞不处理)
  └── LLM 分析 → 创建 OptimizationTicket
```

### 为什么用 Spring Event 而不是直接调用？

| 方式 | 耦合度 | 失败影响 |
|------|:--:|------|
| 直接调用 Service | Controller 依赖 BadCaseAutoTicketService | 工单创建失败 → feedback 响应 500 |
| **Spring Event** | Controller 仅依赖 EventPublisher | 工单创建失败 → feedback 正常返回 |

事件模式下 BadCase 分析和工单创建失败不会影响用户收到反馈确认，实现 fail-open。
