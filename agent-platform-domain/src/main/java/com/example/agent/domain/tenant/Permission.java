package com.example.agent.domain.tenant;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 权限领域实体
 */
@Getter
@Builder
public class Permission {

    private Long id;
    private String permissionCode;
    private String resource;
    private String action;   // READ / WRITE / DELETE / ADMIN / PUBLISH
    private String description;
    private LocalDateTime createdAt;
    private Boolean deleted;
}
