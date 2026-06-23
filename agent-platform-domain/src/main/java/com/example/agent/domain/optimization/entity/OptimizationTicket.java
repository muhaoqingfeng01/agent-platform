package com.example.agent.domain.optimization.entity;

import com.example.agent.domain.optimization.valueobject.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 优化工单实体 — BadCase 闭环.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class OptimizationTicket {

    private Long id;
    private Long tenantId;
    private String ticketId;
    private String conversationId;
    private String messageId;
    private String issueType;
    private String severity;
    private String description;
    private String assignee;
    private TicketStatus status;
    private String resolution;
    private String resolutionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void assign(String userId) {
        this.assignee = userId;
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void resolve(String resolution, String resolutionType) {
        this.resolution = resolution;
        this.resolutionType = resolutionType;
        this.status = TicketStatus.RESOLVED;
    }

    public void close() {
        this.status = TicketStatus.CLOSED;
    }

    public void startAnalysis() {
        this.status = TicketStatus.ANALYZING;
    }
}
