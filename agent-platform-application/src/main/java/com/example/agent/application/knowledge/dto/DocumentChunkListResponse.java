package com.example.agent.application.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文档切片列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档切片列表")
public class DocumentChunkListResponse {

    @Schema(description = "切片总数")
    private int total;

    @Schema(description = "切片列表")
    private List<?> chunks;
}
