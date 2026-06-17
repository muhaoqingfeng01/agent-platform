package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 敏感词持久化对象 — 映射 t_sensitive_word 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordPO {

    private Long id;
    private Long tenantId;
    private String word;
    private String matchType;
    private String category;
    private String severity;
    private String action;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
