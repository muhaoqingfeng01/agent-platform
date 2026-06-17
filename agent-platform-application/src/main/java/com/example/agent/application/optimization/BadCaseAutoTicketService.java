package com.example.agent.application.optimization;

import com.example.agent.application.optimization.event.MessageFeedbackEvent;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.repository.MessageRepository;
import com.example.agent.domain.conversation.valueobject.FeedbackType;
import com.example.agent.domain.optimization.entity.OptimizationTicket;
import com.example.agent.domain.optimization.repository.OptimizationTicketRepository;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * BadCase 自动工单服务 — 监听消息点踩事件，自动分析并创建优化工单.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BadCaseAutoTicketService {

    private final OptimizationTicketRepository ticketRepository;
    private final MessageRepository messageRepository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    /**
     * 监听消息点踩事件，LLM 自动分析问题类型并生成工单.
     */
    @EventListener
    public void onMessageDisliked(MessageFeedbackEvent event) {
        if (event.getFeedback() != FeedbackType.DISLIKE) return;

        try {
            String messageId = event.getMessageId();
            String conversationId = event.getConversationId();
            String tenantId = event.getTenantId();

            // 获取被点踩的消息上下文
            List<Message> context = messageRepository.findByConversationId(conversationId, 0, 5);

            // LLM 分析问题
            String analysis = analyzeIssue(context, messageId);
            IssueAnalysisResult result = parseAnalysis(analysis);

            // 创建工单
            OptimizationTicket ticket = OptimizationTicket.builder()
                    .tenantId(tenantId)
                    .ticketId(IdGenerator.generate("ticket"))
                    .conversationId(conversationId).messageId(messageId)
                    .issueType(result.type).severity(result.severity)
                    .description(result.description).status("OPEN").build();
            ticketRepository.save(ticket);

            log.info("[BadCase] 自动工单已创建: ticketId={}, issueType={}, severity={}",
                    ticket.getTicketId(), result.type, result.severity);

        } catch (Exception e) {
            log.warn("[BadCase] 自动工单生成失败（已忽略）: messageId={}, error={}",
                    event.getMessageId(), e.getMessage());
        }
    }

    private String analyzeIssue(List<Message> context, String dislikedMsgId) {
        StringBuilder ctxBuilder = new StringBuilder();
        for (Message msg : context) {
            ctxBuilder.append(msg.getRole().getLabel()).append(": ")
                    .append(truncate(msg.getContent(), 200)).append("\n");
        }

        String prompt = String.format("""
                分析以下 AI 对话中被用户点踩的回复，判断问题类型和严重程度。

                对话上下文:
                %s

                被点踩的消息 ID: %s

                请输出 JSON:
                {"type": "HALLUCINATION|IRRELEVANT|INCOMPLETE|WRONG|OTHER",
                 "severity": "LOW|MEDIUM|HIGH|CRITICAL",
                 "description": "简要描述问题（中文，不超过100字）"}
                只输出 JSON。""", ctxBuilder.toString(), dislikedMsgId);

        return chatClient.prompt().user(prompt).call().content();
    }

    private IssueAnalysisResult parseAnalysis(String json) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(extractJson(json), Map.class);
            return new IssueAnalysisResult(
                    (String) map.getOrDefault("type", "OTHER"),
                    (String) map.getOrDefault("severity", "MEDIUM"),
                    (String) map.getOrDefault("description", "用户点踩反馈")
            );
        } catch (Exception e) {
            return new IssueAnalysisResult("OTHER", "MEDIUM", "用户点踩反馈");
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return "{}";
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }

    record IssueAnalysisResult(String type, String severity, String description) {}
}
