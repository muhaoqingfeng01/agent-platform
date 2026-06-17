package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 提示词模板持久化对象 — 映射 t_prompt_template 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplatePO {

    private Long id;
    private Long tenantId;
    private String promptId;
    private String name;
    private String description;
    private String templateText;
    private String variablesJson;   // JSON 字符串
    private Integer version;
    private String status;          // DRAFT / PUBLISHED / ARCHIVED
    private String abTestConfig;    // JSON
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
