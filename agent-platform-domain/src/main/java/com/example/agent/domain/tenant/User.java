package com.example.agent.domain.tenant;

import com.example.agent.domain.security.UserView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户领域实体（扩展了 UserView）
 */
@Getter
@Setter
@Builder
public class User {

    private Long id;
    private Long tenantId;
    private String userId;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String status;   // ACTIVE / DISABLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public UserView toView() {
        UserView view = new UserView();
        view.setUserId(userId);
        view.setUsername(username);
        view.setPasswordHash(passwordHash);
        view.setTenantId(tenantId);
        view.setStatus(status);
        return view;
    }
}
