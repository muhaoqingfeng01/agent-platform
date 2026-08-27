package com.example.agent.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户唯一标识 */
    private String userId;

    /** 用户名 */
    private String username;

    /** 所属租户 */
    private Long tenantId;

    /** 角色编码列表 */
    private List<String> roles;

    /** 权限码列表（前端据此隐藏知识检索等入口） */
    private List<String> permissions;

    public UserInfo(String userId, String username, Long tenantId) {
        this.userId = userId;
        this.username = username;
        this.tenantId = tenantId;
    }
}
