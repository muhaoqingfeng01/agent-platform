package com.example.agent.application.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 敏感词规则列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "敏感词规则列表")
public class SensitiveWordListResponse {

    @Schema(description = "敏感词规则列表")
    private List<SensitiveWordResponse> records;
}
