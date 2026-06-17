package com.example.agent.domain.security;

import java.util.List;

/**
 * 用户领域服务接口（脚手架占位）
 * <p>
 * P0 阶段仅提供编译占位，具体 CRUD 在 T8 多租户模块实现。
 */
public interface UserService {

    /**
     * 校验用户名密码
     *
     * @param tenantId 租户标识（可选，为 null 时跨租户查找）
     * @param username 用户名
     * @param password 明文密码
     * @return 用户视图对象，认证失败返回 null
     */
    UserView authenticate(Long tenantId, String username, String password);

    /**
     * 获取用户角色编码列表
     *
     * @param userId 用户唯一标识
     * @return 角色编码列表
     */
    List<String> getRoleCodes(String userId);
}
