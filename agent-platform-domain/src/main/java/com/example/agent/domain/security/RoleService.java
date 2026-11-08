package com.example.agent.domain.security;

import java.util.List;

/**
 * 角色领域服务接口（脚手架占位）
 * <p>
 * P0 阶段仅提供编译占位，具体 CRUD 在 T8 多租户模块实现。
 */
public interface RoleService {

    /**
     * 获取用户角色编码列表（优先读缓存）
     *
     * @param userId 用户唯一标识
     * @return 角色编码列表（如 "ADMIN", "OPERATOR"）
     */
    List<String> getRoleCodes(String userId);

    /**
     * 强制从数据库重载角色并覆盖缓存（不踢下线）
     *
     * @param userId 用户唯一标识
     * @return 最新角色编码列表
     */
    List<String> refreshRoleCodes(String userId);
}
