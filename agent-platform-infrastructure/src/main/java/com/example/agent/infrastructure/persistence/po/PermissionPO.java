package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限持久化对象 — 映射 t_permission 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionPO {

    private Long id;
    private String permissionCode;
    private String resource;
    private String action;
    private String description;
    private LocalDateTime createdAt;
    private Boolean deleted;
}
