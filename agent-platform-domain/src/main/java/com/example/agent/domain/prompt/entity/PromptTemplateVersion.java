package com.example.agent.domain.prompt.entity;

import com.example.agent.domain.prompt.valueobject.VariableDef;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提示词版本历史实体 — 每次发布/回滚生成一条记录.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder
public class PromptTemplateVersion {

    private Long id;
    private String promptId;        // 关联模板
    private Integer version;        // 版本号
    private String templateText;    // 该版本模板全文
    private List<VariableDef> variables; // 该版本变量定义
    private String changeLog;       // 变更说明
    private String publisher;       // 发布人
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}
