package com.example.agent.domain.tenant;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 角色领域实体
 */
@Getter
@Builder
public class Role {

    private Long id;
    private Long tenantId;
    private String roleCode;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
