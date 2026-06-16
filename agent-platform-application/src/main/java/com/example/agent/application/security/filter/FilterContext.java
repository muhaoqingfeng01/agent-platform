package com.example.agent.application.security.filter;

import lombok.Builder;
import lombok.Data;

/**
 * 过滤上下文 — 携带当前请求的会话和用户信息.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
public class FilterContext {

    /** 会话 ID */
    private String conversationId;

    /** 消息 ID */
    private String messageId;

    /** 租户 ID */
    private String tenantId;

    /** 用户 ID */
    private String userId;
}
