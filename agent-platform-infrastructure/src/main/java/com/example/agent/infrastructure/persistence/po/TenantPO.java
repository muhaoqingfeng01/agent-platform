package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 租户持久化对象 — 映射 t_tenant 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantPO {

    private Long id;
    private Long tenantId;
    private String name;
    private String status;
    private String tier;
    private String configJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
