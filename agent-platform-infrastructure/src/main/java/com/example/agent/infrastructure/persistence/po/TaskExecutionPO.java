package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务执行持久化对象 — 映射 t_task_execution 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionPO {

    private Long id;
    private Long tenantId;
    private String executionId;
    private String conversationId;
    private String agentId;
    private String planJson;
    private String status;
    private Integer totalSteps;
    private Integer completedSteps;
    private String failedStepId;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
