package com.example.agent.interfaces.dto.request.optimization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "工单ID请求")
public class TicketGetRequest {
    @NotBlank(message = "工单ID不能为空")
    @Schema(description = "工单ID")
    private String ticketId;
}
