package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口（领域层）
 */
public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByUserId(String userId);

    Optional<User> findByTenantAndUsername(String tenantId, String username);

    List<User> findByTenant(String tenantId, int page, int size);

    long countByTenant(String tenantId);

    void save(User user);

    void update(User user);

    void delete(Long id);
}
