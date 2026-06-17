package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationTicketPO {
    private Long id;
    private Long tenantId;
    private String ticketId;
    private String conversationId;
    private String messageId;
    private String issueType;
    private String severity;
    private String description;
    private String assignee;
    private String status;
    private String resolution;
    private String resolutionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
