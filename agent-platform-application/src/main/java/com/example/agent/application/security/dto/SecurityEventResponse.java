package com.example.agent.application.security.dto;

import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.security.entity.SecurityEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 安全事件响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "安全事件")
public class SecurityEventResponse {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "所属租户 ID")
    private Long tenantId;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "触发的规则 ID")
    private Long ruleId;

    @Schema(description = "关联会话 ID")
    private String conversationId;

    @Schema(description = "关联消息 ID")
    private String messageId;

    @Schema(description = "原始内容（截断）")
    private String originalContent;

    @Schema(description = "处理后内容")
    private String processedContent;

    @Schema(description = "匹配到的模式")
    private String matchedPattern;

    @Schema(description = "采取动作")
    private String actionTaken;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "创建时间")
    private Long createdAt;

    public static SecurityEventResponse from(SecurityEvent entity) {
        return SecurityEventResponse.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .eventType(entity.getEventType())
                .ruleId(entity.getRuleId())
                .conversationId(entity.getConversationId())
                .messageId(entity.getMessageId())
                .originalContent(entity.getOriginalContent() != null && entity.getOriginalContent().length() > 200
                        ? entity.getOriginalContent().substring(0, 200) + "..."
                        : entity.getOriginalContent())
                .processedContent(entity.getProcessedContent())
                .matchedPattern(entity.getMatchedPattern())
                .actionTaken(entity.getActionTaken())
                .operator(entity.getOperator())
                .createdAt(TimeConverters.toEpochMilli(entity.getCreatedAt()))
                .build();
    }
}
