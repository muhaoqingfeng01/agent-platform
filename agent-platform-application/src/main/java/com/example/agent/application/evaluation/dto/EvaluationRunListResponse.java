package com.example.agent.application.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 评测运行列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评测运行列表")
public class EvaluationRunListResponse {

    @Schema(description = "评测运行列表")
    private List<EvaluationRunResponse> records;
}
