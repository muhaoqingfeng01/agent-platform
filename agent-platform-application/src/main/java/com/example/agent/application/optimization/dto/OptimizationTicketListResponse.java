package com.example.agent.application.optimization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 优化工单列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "优化工单列表")
public class OptimizationTicketListResponse {

    @Schema(description = "优化工单列表")
    private List<OptimizationTicketResponse> records;
}
