# BadCase 闭环与优化工单

## 所属阶段
**P4 观测优化 → T12 效果评估与持续优化**

## 使用技术
- 消息反馈（点赞/点踩）自动触发
- 优化工单自动分配
- 工单状态流转追踪

## 涉及数据库表
- `t_optimization_ticket`, `t_message`（feedback 字段）

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/optimization-tickets` | 工单列表 |
| GET | `/api/v1/optimization-tickets/{id}` | 工单详情 |
| PATCH | `/api/v1/optimization-tickets/{id}/assign` | 指派处理人 |
| PATCH | `/api/v1/optimization-tickets/{id}/status` | 更新状态 |
| POST | `/api/v1/optimization-tickets/{id}/resolve` | 提交解决方案 |
| GET | `/api/v1/feedback/stats` | 反馈统计面板 |

## 实现方案

### 1. 从点踩到工单的自动流转

```java
@Component
public class BadCaseAutoTicketService {

    /**
     * 监听消息点踩事件，自动生成优化工单
     */
    @EventListener
    public void onMessageDisliked(MessageFeedbackEvent event) {
        if (!"DISLIKE".equals(event.getFeedback())) return;

        // 获取被点踩的消息上下文
        Message dislikedMsg = messageRepository.findByMessageId(event.getMessageId());
        List<Message> context = messageRepository.findRecentMessages(
                dislikedMsg.getConversationId(), 5);

        // LLM 自动分析问题类型
        String analysis = analyzeIssue(context, dislikedMsg);
        IssueAnalysis issue = parseIssueAnalysis(analysis);

        // 创建工单
        OptimizationTicket ticket = OptimizationTicket.builder()
                .ticketId(IdGenerator.generate("ticket"))
                .tenantId(TenantContext.getCurrentTenantId())
                .conversationId(event.getConversationId())
                .messageId(event.getMessageId())
                .issueType(issue.getType())   // HALLUCINATION / IRRELEVANT / INCOMPLETE / WRONG
                .severity(issue.getSeverity()) // LOW / MEDIUM / HIGH / CRITICAL
                .description(issue.getDescription())
                .status("OPEN")
                .build();
        ticketRepository.save(ticket);

        // 自动分配给知识库管理员
        autoAssign(ticket);
    }

    private String analyzeIssue(List<Message> context, Message disliked) {
        return chatClient.prompt().user("""
            分析以下 AI 对话中被用户点踩的回复，判断问题类型和严重程度。

            对话上下文:
            {context}

            被点踩的回复:
            {disliked}

            输出 JSON:
            {"type": "HALLUCINATION|IRRELEVANT|INCOMPLETE|WRONG|OTHER",
             "severity": "LOW|MEDIUM|HIGH|CRITICAL",
             "description": "简要描述问题"}
            """).call().content();
    }
}
```

### 2. 工单状态流转

```
OPEN → ANALYZING → IN_PROGRESS → RESOLVED → CLOSED
  │        │            │
  └────────┴────────────┴──→ CLOSED (直接关闭)
```

```java
public enum TicketStatus {
    OPEN("待处理"),
    ANALYZING("分析中"),
    IN_PROGRESS("处理中"),
    RESOLVED("已解决"),
    CLOSED("已关闭");

    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case OPEN        -> target == ANALYZING || target == IN_PROGRESS || target == CLOSED;
            case ANALYZING   -> target == IN_PROGRESS || target == CLOSED;
            case IN_PROGRESS -> target == RESOLVED || target == CLOSED;
            case RESOLVED    -> target == CLOSED;
            case CLOSED      -> false;  // 终态
        };
    }
}
```

### 3. 解决方案类型追踪

| 解决类型 | 说明 | 后续动作 |
|----------|------|----------|
| `KNOWLEDGE_FIX` | 知识库内容修正 | 更新知识库/文档，重新向量化 |
| `PROMPT_ADJUST` | 提示词调整 | 创建提示词新版本 |
| `MODEL_FINETUNE` | 积累微调数据 | 沉淀为微调数据集 |
| `OTHER` | 其他 | 记录备注 |

### 4. 反馈统计 Dashboard API

```java
@GetMapping("/api/v1/feedback/stats")
public Result<FeedbackStats> getStats(@RequestParam(defaultValue = "7") int days) {
    LocalDateTime since = LocalDateTime.now().minusDays(days);

    long likes = messageRepository.countFeedback("LIKE", since);
    long dislikes = messageRepository.countFeedback("DISLIKE", since);
    double likeRate = (double) likes / (likes + dislikes) * 100;

    // 问题分布
    Map<String, Long> issueDistribution = ticketRepository.countByIssueType(since);

    // 工单解决率
    long total = ticketRepository.countByCreatedSince(since);
    long resolved = ticketRepository.countByStatusAndResolvedSince("RESOLVED", since);
    double resolveRate = total > 0 ? (double) resolved / total * 100 : 0;

    return Result.success(new FeedbackStats(likes, dislikes, likeRate,
            issueDistribution, resolveRate, total, resolved));
}
```
