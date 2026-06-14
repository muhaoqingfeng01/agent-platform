package com.example.agent.infrastructure.persistence.stub;

import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 租户仓储脚手架实现（ConcurrentHashMap）
 *
 * @deprecated 已由 {@link com.example.agent.infrastructure.persistence.impl.TenantRepositoryImpl} 替代
 */
@Deprecated
@Slf4j
// @Repository — 已由 TenantRepositoryImpl 替代
public class TenantRepositoryStub implements TenantRepository {

    private final ConcurrentHashMap<Long, Tenant> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Optional<Tenant> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Tenant> findByTenantId(String tenantId) {
        return store.values().stream()
                .filter(t -> t.getTenantId().equals(tenantId) && !t.getDeleted())
                .findFirst();
    }

    @Override
    public List<Tenant> findAll(int page, int size) {
        return store.values().stream()
                .filter(t -> !t.getDeleted())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long count() {
        return store.values().stream().filter(t -> !t.getDeleted()).count();
    }

    @Override
    public void save(Tenant tenant) {
        long id = idGen.getAndIncrement();
        Tenant saved = Tenant.builder()
                .id(id).tenantId(tenant.getTenantId()).name(tenant.getName())
                .status(tenant.getStatus()).tier(tenant.getTier())
                .configJson(tenant.getConfigJson())
                .createdAt(tenant.getCreatedAt()).updatedAt(tenant.getUpdatedAt())
                .deleted(false)
                .build();
        store.put(id, saved);
        tenant = saved; // 回填 ID（通过反射或重新构建）
        log.info("[TenantRepoStub] 租户已保存: id={}, tenantId={}", id, saved.getTenantId());
    }

    @Override
    public void update(Tenant tenant) {
        store.put(tenant.getId(), tenant);
        log.debug("[TenantRepoStub] 租户已更新: id={}", tenant.getId());
    }

    @Override
    public void delete(Long id) {
        Tenant existing = store.get(id);
        if (existing != null) {
            Tenant deleted = Tenant.builder()
                    .id(existing.getId()).tenantId(existing.getTenantId()).name(existing.getName())
                    .status(existing.getStatus()).tier(existing.getTier())
                    .configJson(existing.getConfigJson())
                    .createdAt(existing.getCreatedAt()).updatedAt(existing.getUpdatedAt())
                    .deleted(true)
                    .build();
            store.put(id, deleted);
            log.info("[TenantRepoStub] 租户已逻辑删除: id={}", id);
        }
    }
}
