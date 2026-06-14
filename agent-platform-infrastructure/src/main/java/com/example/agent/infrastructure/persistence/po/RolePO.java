package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色持久化对象 — 映射 t_role 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePO {

    private Long id;
    private String tenantId;
    private String roleCode;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
