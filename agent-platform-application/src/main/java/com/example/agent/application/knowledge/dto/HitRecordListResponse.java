package com.example.agent.application.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 命中记录列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "命中记录列表")
public class HitRecordListResponse {

    @Schema(description = "命中记录列表")
    private List<HitRecordDTO> records;
}
