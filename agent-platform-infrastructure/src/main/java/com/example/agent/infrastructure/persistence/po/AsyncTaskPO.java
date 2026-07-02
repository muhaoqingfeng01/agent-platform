package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 异步任务持久化对象 — 映射 t_async_task 表.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskPO {
    private Long id;
    private String taskId;
    private String taskType;
    private String bizId;
    private Long tenantId;
    private String status;
    private String payloadJson;
    private String resultJson;
    private LocalDateTime timeoutAt;
    private Integer retryCount;
    private Integer maxRetries;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
