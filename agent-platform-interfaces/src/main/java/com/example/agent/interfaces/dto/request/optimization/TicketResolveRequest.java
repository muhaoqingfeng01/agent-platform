package com.example.agent.interfaces.dto.request.optimization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "提交解决方案请求（含ID）")
public class TicketResolveRequest {
    @NotBlank(message = "工单ID不能为空")
    @Schema(description = "工单ID")
    private String ticketId;
    @Schema(description = "解决方案")
    private String resolution;
    @Schema(description = "解决方案类型")
    private String resolutionType;
}
