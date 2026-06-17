package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口（领域层）
 */
public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByUserId(String userId);

    Optional<User> findByTenantAndUsername(Long tenantId, String username);

    /**
     * 跨租户用户名查找（用于未指定租户时的认证兜底）.
     * <p>当存在同名用户时返回第一条匹配记录，生产环境应确保用户名全局唯一或在请求中携带 tenantId.
     */
    Optional<User> findByUsername(String username);

    List<User> findByTenant(Long tenantId, int page, int size);

    long countByTenant(Long tenantId);

    void save(User user);

    void update(User user);

    void delete(Long id);
}
