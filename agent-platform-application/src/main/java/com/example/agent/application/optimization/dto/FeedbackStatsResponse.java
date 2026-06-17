package com.example.agent.application.optimization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatsResponse {
    private long totalTickets;
    private long resolvedTickets;
    private String resolveRate;
    private int periodDays;
}
