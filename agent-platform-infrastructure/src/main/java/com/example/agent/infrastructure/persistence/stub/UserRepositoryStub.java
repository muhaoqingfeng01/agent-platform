package com.example.agent.infrastructure.persistence.stub;

import com.example.agent.domain.tenant.User;
import com.example.agent.domain.tenant.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户仓储脚手架实现（已废弃，由 UserRepositoryImpl 替代）
 */
@Slf4j
// @Repository — 已由 UserRepositoryImpl 替代
public class UserRepositoryStub implements UserRepository {

    private final ConcurrentHashMap<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return store.values().stream()
                .filter(u -> u.getUserId().equals(userId) && !u.getDeleted())
                .findFirst();
    }

    @Override
    public Optional<User> findByTenantAndUsername(String tenantId, String username) {
        return store.values().stream()
                .filter(u -> u.getTenantId().equals(tenantId)
                        && u.getUsername().equals(username) && !u.getDeleted())
                .findFirst();
    }

    @Override
    public List<User> findByTenant(String tenantId, int page, int size) {
        return store.values().stream()
                .filter(u -> u.getTenantId().equals(tenantId) && !u.getDeleted())
                .skip((long) page * size).limit(size).toList();
    }

    @Override
    public long countByTenant(String tenantId) {
        return store.values().stream()
                .filter(u -> u.getTenantId().equals(tenantId) && !u.getDeleted()).count();
    }

    @Override
    public void save(User user) {
        long id = idGen.getAndIncrement();
        User saved = User.builder()
                .id(id).tenantId(user.getTenantId()).userId(user.getUserId())
                .username(user.getUsername()).passwordHash(user.getPasswordHash())
                .email(user.getEmail()).phone(user.getPhone())
                .status(user.getStatus()).createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt()).deleted(false)
                .build();
        store.put(id, saved);
        log.info("[UserRepoStub] 用户已保存: id={}, userId={}", id, saved.getUserId());
    }

    @Override
    public void update(User user) {
        store.put(user.getId(), user);
        log.debug("[UserRepoStub] 用户已更新: id={}", user.getId());
    }

    @Override
    public void delete(Long id) {
        User existing = store.get(id);
        if (existing != null) {
            User u = User.builder()
                    .id(existing.getId()).tenantId(existing.getTenantId())
                    .userId(existing.getUserId()).username(existing.getUsername())
                    .passwordHash(existing.getPasswordHash())
                    .email(existing.getEmail()).phone(existing.getPhone())
                    .status(existing.getStatus()).createdAt(existing.getCreatedAt())
                    .updatedAt(existing.getUpdatedAt()).deleted(true)
                    .build();
            store.put(id, u);
        }
    }
}
