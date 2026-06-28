package com.example.agent.interfaces.dto.request.optimization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "指派工单请求（含ID）")
public class TicketAssignRequest {
    @NotBlank(message = "工单ID不能为空")
    @Schema(description = "工单ID")
    private String ticketId;
    @Schema(description = "处理人")
    private String assignee;
}
