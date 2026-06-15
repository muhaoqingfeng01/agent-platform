package com.example.agent.application.knowledge.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 切片配置 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
public class ChunkConfigDTO {
    private String chunkStrategy;
    private int chunkSize;
    private int chunkOverlap;
    private Map<String, Object> chunkConfig;
}
