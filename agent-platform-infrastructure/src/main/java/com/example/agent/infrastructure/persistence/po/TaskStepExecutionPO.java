package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务步骤执行持久化对象 — 映射 t_task_step_execution 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStepExecutionPO {

    private Long id;
    private String executionId;
    private String stepId;
    private String action;
    private String handlerClass;
    private String inputJson;
    private String outputJson;
    private String status;
    private Integer retryCount;
    private Integer maxRetries;
    private Long durationMs;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
}
