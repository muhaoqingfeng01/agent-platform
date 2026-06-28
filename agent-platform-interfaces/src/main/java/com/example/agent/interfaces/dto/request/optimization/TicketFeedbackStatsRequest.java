package com.example.agent.interfaces.dto.request.optimization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "反馈统计请求")
public class TicketFeedbackStatsRequest {
    @Schema(description = "统计天数", example = "7")
    private int days = 7;
}
