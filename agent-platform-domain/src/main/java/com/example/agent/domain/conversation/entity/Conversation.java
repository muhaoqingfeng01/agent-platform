package com.example.agent.domain.conversation.entity;

import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 会话聚合根 — State 模式管理状态流转.
 * <p>
 * Builder 模式构建，禁止直接 setter.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class Conversation {

    private String conversationId;
    private Long tenantId;
    private String agentId;
    private String userId;
    private String title;
    private ConversationStatus status;
    private int messageCount;
    private long totalTokens;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ========== State 模式：状态流转 ==========

    /** 关闭会话 — 仅 ACTIVE 可关闭 */
    public void close() {
        if (this.status != ConversationStatus.ACTIVE) {
            throw new IllegalStateException("仅 ACTIVE 会话可关闭，当前: " + this.status);
        }
        this.status = ConversationStatus.CLOSED;
    }

    /** 重开会话 — 仅 CLOSED 可重开 */
    public void reopen() {
        if (this.status != ConversationStatus.CLOSED) {
            throw new IllegalStateException("仅 CLOSED 会话可重开，当前: " + this.status);
        }
        this.status = ConversationStatus.ACTIVE;
    }

    /** 归档会话 — ACTIVE/CLOSED 均可归档，不可逆 */
    public void archive() {
        if (this.status == ConversationStatus.ARCHIVED) {
            throw new IllegalStateException("已归档的会话不可再次归档");
        }
        this.status = ConversationStatus.ARCHIVED;
    }

    /** 增量更新消息计数和 Token */
    public void incrementMessage(int tokenCount) {
        this.messageCount++;
        this.totalTokens += tokenCount;
    }

    /** 更新标题 */
    public void updateTitle(String newTitle) {
        if (newTitle != null && !newTitle.isBlank() && !newTitle.equals(this.title)) {
            this.title = newTitle;
        }
    }

    /** 是否可收发消息 */
    public boolean canReceiveMessage() {
        return this.status == ConversationStatus.ACTIVE;
    }
}
