package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 提示词版本历史持久化对象 — 映射 t_prompt_template_version 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplateVersionPO {

    private Long id;
    private String promptId;
    private Integer version;
    private String templateText;
    private String variablesJson;   // JSON 字符串
    private String changeLog;
    private String publisher;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}
