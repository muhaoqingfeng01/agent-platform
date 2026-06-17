package com.example.agent.domain.prompt.entity;

import com.example.agent.domain.prompt.valueobject.PromptStatus;
import com.example.agent.domain.prompt.valueobject.VariableDef;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提示词模板聚合根.
 * <p>
 * 封装模板内容、变量定义、版本状态，是提示词管理的核心领域对象。
 * 版本变更通过 {@link com.example.agent.domain.prompt.service.PromptDomainService} 操作。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
public class PromptTemplate {

    private Long id;
    private Long tenantId;
    private String promptId;        // 业务唯一标识
    private String name;            // 模板名称
    private String description;     // 描述
    private String templateText;    // 含 {{variable}} 占位符的模板文本
    private List<VariableDef> variables; // 变量定义列表
    private Integer version;        // 当前发布版本号
    private PromptStatus status;    // DRAFT / PUBLISHED / ARCHIVED
    private String abTestConfig;    // A/B 测试配置 JSON
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    /** 是否可发布 */
    public boolean isPublishable() {
        return status == PromptStatus.DRAFT;
    }

    /** 是否已发布 */
    public boolean isPublished() {
        return status == PromptStatus.PUBLISHED;
    }

    /** 获取当前版本号，未发布过则为 0 */
    public int getCurrentOrZero() {
        return version != null ? version : 0;
    }
}
