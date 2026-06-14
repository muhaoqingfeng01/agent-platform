package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 租户仓储接口（领域层）
 * <p>
 * 实现在 infrastructure 层通过 MyBatis-Plus 完成。
 */
public interface TenantRepository {

    Optional<Tenant> findById(Long id);

    Optional<Tenant> findByTenantId(String tenantId);

    List<Tenant> findAll(int page, int size);

    long count();

    void save(Tenant tenant);

    void update(Tenant tenant);

    void delete(Long id);
}
