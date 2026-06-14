package com.example.agent.application.user;

import com.example.agent.domain.tenant.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    @Schema(description = "主键 ID") private Long id;
    @Schema(description = "所属租户") private String tenantId;
    @Schema(description = "用户唯一标识") private String userId;
    @Schema(description = "用户名") private String username;
    @Schema(description = "邮箱") private String email;
    @Schema(description = "手机号") private String phone;
    @Schema(description = "状态") private String status;
    @Schema(description = "创建时间") private LocalDateTime createdAt;

    public static UserResponse from(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setTenantId(u.getTenantId());
        r.setUserId(u.getUserId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setPhone(u.getPhone());
        r.setStatus(u.getStatus());
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}
