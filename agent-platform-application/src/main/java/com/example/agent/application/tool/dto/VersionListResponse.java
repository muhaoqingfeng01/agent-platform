package com.example.agent.application.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 工具版本历史列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "工具版本历史列表")
public class VersionListResponse {

    @Schema(description = "版本历史列表")
    private List<VersionResponse> records;
}
