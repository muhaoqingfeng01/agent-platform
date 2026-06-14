package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 长期记忆持久化对象 — 映射 t_long_term_memory 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LongTermMemoryPO {

    private Long id;
    private String tenantId;
    private String userId;
    private String memoryType;
    private String memoryKey;
    private String memoryValue;
    private Double confidence;
    private String source;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
