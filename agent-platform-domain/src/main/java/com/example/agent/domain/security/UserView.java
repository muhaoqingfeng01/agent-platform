package com.example.agent.domain.security;

import com.example.agent.domain.tenant.valueobject.UserStatusEnums;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户视图对象（领域层简单 VO，不含持久化注解）
 */
@Getter
@Setter
public class UserView implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户唯一标识 */
    private String userId;

    /** 用户名 */
    private String username;

    /** 密码哈希 */
    private String passwordHash;

    /** 所属租户 */
    private Long tenantId;

    /** 状态 */
    private UserStatusEnums status;

    /** 是否活跃 */
    public boolean isActive() {
        return status != null && status.isActive();
    }
}
