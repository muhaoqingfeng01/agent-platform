package com.example.agent.application.security;

import com.example.agent.application.security.filter.FilterContext;
import com.example.agent.application.security.filter.FilterResult;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.domain.security.entity.SecurityEvent;
import com.example.agent.domain.security.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 安全事件记录器 — Observer 模式，在过滤/脱敏操作后异步记录审计日志.
 *
 * <p>每次安全过滤（阻断/告警）和输出脱敏操作都会写入 t_security_event 表，
 * 用于合规审计和攻击追溯。
 * <p>高严重度事件（BLOCK 级别）会触发告警通知。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityEventRecorder {

    private final SecurityEventRepository securityEventRepository;

    // 预留：告警通知服务注入点
    // private final AlertService alertService;

    /**
     * 记录输入过滤事件.
     *
     * @param result  过滤结果
     * @param context 过滤上下文
     */
    public void record(FilterResult result, FilterContext context) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                    .tenantId(context.getTenantId() != null ? context.getTenantId()
                            : TenantContext.getCurrentTenantId())
                    .eventType(result.getEventType())
                    .ruleId(null) // 由过滤器填充
                    .conversationId(context.getConversationId())
                    .messageId(context.getMessageId())
                    .originalContent(null) // 原始内容由调用方在 recordInputBlock 中设置
                    .processedContent(null)
                    .matchedPattern(result.getMatchedPattern())
                    .actionTaken(result.isBlocked() ? "BLOCK" : "LOG")
                    .operator("SYSTEM")
                    .createdAt(LocalDateTime.now())
                    .build();
            securityEventRepository.save(event);

            if (result.isBlocked()) {
                log.warn("[SecurityEventRecorder] 安全阻断: eventType={}, reason={}, conversationId={}",
                        result.getEventType(), result.getReason(), context.getConversationId());

                // 高危事件触发告警
                if ("HIGH".equalsIgnoreCase(result.getSeverity())
                        || "BLOCK".equalsIgnoreCase(result.getSeverity())) {
                    sendAlert(result, context);
                }
            }
        } catch (Exception e) {
            // 事件记录失败不应影响主流程
            log.error("[SecurityEventRecorder] 记录安全事件失败", e);
        }
    }

    /**
     * 记录输出脱敏事件.
     *
     * @param tenantId        租户 ID
     * @param conversationId  会话 ID
     * @param messageId       消息 ID
     * @param originalContent 脱敏前内容
     * @param desensitizedContent 脱敏后内容
     * @param piiTypes        检测到的 PII 类型列表
     */
    public void recordDesensitize(String tenantId, String conversationId, String messageId,
                                   String originalContent, String desensitizedContent,
                                   java.util.List<String> piiTypes) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                    .tenantId(tenantId)
                    .eventType("OUTPUT_DESENSITIZE")
                    .conversationId(conversationId)
                    .messageId(messageId)
                    .originalContent(truncate(originalContent, 1000))
                    .processedContent(truncate(desensitizedContent, 1000))
                    .matchedPattern(String.join(",", piiTypes))
                    .actionTaken("LOG")
                    .operator("SYSTEM")
                    .createdAt(LocalDateTime.now())
                    .build();
            securityEventRepository.save(event);

            log.debug("[SecurityEventRecorder] PII 脱敏记录: piiTypes={}, conversationId={}",
                    piiTypes, conversationId);
        } catch (Exception e) {
            log.error("[SecurityEventRecorder] 记录脱敏事件失败", e);
        }
    }

    /**
     * 高危事件告警通知.
     */
    private void sendAlert(FilterResult result, FilterContext context) {
        // TODO: 集成告警通知服务（Telegram/邮件/站内信）
        log.warn("[SecurityEventRecorder] 高危安全阻断: eventType={}, reason={}, userId={}, conversationId={}",
                result.getEventType(), result.getReason(), context.getUserId(), context.getConversationId());
    }

    /**
     * 截断内容 — 防止超长文本撑爆存储.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return null;
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...(truncated)";
    }
}
