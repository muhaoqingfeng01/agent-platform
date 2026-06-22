package com.example.agent.application.optimization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationTicketResponse {
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
    private Long createdAt;
    private Long updatedAt;
}
