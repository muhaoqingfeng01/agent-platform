package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工具版本历史持久化对象 — 映射 t_tool_registry_version 表.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolRegistryVersionPO {

    private Long id;
    private String tenantId;
    private String toolId;
    private Integer version;
    private String toolName;
    private String toolType;
    private String endpointUrl;
    private String inputSchema;
    private String outputSchema;
    private String authConfigJson;
    private Boolean requireApproval;
    private String description;
    private String changeReason;
    private LocalDateTime createdAt;
}
