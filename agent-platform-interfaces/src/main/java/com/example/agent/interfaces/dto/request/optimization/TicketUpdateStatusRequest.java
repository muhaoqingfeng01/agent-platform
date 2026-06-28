package com.example.agent.interfaces.dto.request.optimization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "更新工单状态请求")
public class TicketUpdateStatusRequest {
    @NotBlank(message = "工单ID不能为空")
    @Schema(description = "工单ID")
    private String ticketId;
    @NotBlank(message = "状态不能为空")
    @Schema(description = "目标状态")
    private String status;
}
