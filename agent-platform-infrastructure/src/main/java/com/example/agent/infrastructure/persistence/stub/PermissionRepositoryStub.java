package com.example.agent.infrastructure.persistence.stub;

import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 权限仓储脚手架实现
 *
 * @deprecated 已由 {@link com.example.agent.infrastructure.persistence.impl.PermissionRepositoryImpl} 替代
 */
@Deprecated
@Slf4j
// @Repository — 已由 PermissionRepositoryImpl 替代
public class PermissionRepositoryStub implements PermissionRepository {

    private final ConcurrentHashMap<Long, Permission> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Optional<Permission> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Permission> findAll() {
        return store.values().stream().filter(p -> !p.getDeleted()).toList();
    }

    @Override
    public List<Permission> findByRoleId(Long roleId) {
        // Stub: role-permission linking handled by RoleRepository
        return List.of();
    }

    @Override
    public List<Permission> findByUserId(String userId) {
        // Stub: user-permission lookup via user_role → role_permission → permission
        return List.of();
    }

    @Override
    public void save(Permission permission) {
        long id = idGen.getAndIncrement();
        Permission saved = Permission.builder()
                .id(id).permissionCode(permission.getPermissionCode())
                .resource(permission.getResource()).action(permission.getAction())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt()).deleted(false)
                .build();
        store.put(id, saved);
    }

    @Override
    public void delete(Long id) {
        Permission p = store.get(id);
        if (p != null) {
            store.put(id, Permission.builder()
                    .id(p.getId()).permissionCode(p.getPermissionCode())
                    .resource(p.getResource()).action(p.getAction())
                    .description(p.getDescription()).createdAt(p.getCreatedAt()).deleted(true)
                    .build());
        }
    }
}
