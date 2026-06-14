package com.example.agent.infrastructure.persistence.stub;

import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 角色仓储脚手架实现
 *
 * @deprecated 已由 {@link com.example.agent.infrastructure.persistence.impl.RoleRepositoryImpl} 替代
 */
@Deprecated
@Slf4j
// @Repository — 已由 RoleRepositoryImpl 替代
public class RoleRepositoryStub implements RoleRepository {

    private final ConcurrentHashMap<Long, Role> roleStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Long>> userRoleStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<Long>> rolePermStore = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(roleStore.get(id));
    }

    @Override
    public List<Role> findByTenant(String tenantId) {
        return roleStore.values().stream()
                .filter(r -> r.getTenantId().equals(tenantId) && !r.getDeleted()).toList();
    }

    @Override
    public List<Role> findByUserId(String userId) {
        List<Long> roleIds = userRoleStore.getOrDefault(userId, new CopyOnWriteArrayList<>());
        return roleIds.stream()
                .map(roleStore::get).filter(Objects::nonNull).filter(r -> !r.getDeleted())
                .toList();
    }

    @Override
    public void save(Role role) {
        long id = idGen.getAndIncrement();
        Role saved = Role.builder()
                .id(id).tenantId(role.getTenantId()).roleCode(role.getRoleCode())
                .roleName(role.getRoleName()).description(role.getDescription())
                .createdAt(role.getCreatedAt()).updatedAt(role.getUpdatedAt()).deleted(false)
                .build();
        roleStore.put(id, saved);
    }

    @Override
    public void update(Role role) {
        roleStore.put(role.getId(), role);
    }

    @Override
    public void delete(Long id) {
        Role r = roleStore.get(id);
        if (r != null) {
            roleStore.put(id, Role.builder()
                    .id(r.getId()).tenantId(r.getTenantId()).roleCode(r.getRoleCode())
                    .roleName(r.getRoleName()).description(r.getDescription())
                    .createdAt(r.getCreatedAt()).updatedAt(r.getUpdatedAt()).deleted(true)
                    .build());
        }
    }

    @Override
    public void assignRoleToUser(String userId, Long roleId) {
        userRoleStore.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(roleId);
    }

    @Override
    public void removeRoleFromUser(String userId, Long roleId) {
        userRoleStore.getOrDefault(userId, new CopyOnWriteArrayList<>()).remove(roleId);
    }

    @Override
    public List<Long> findRoleIdsByUserId(String userId) {
        return userRoleStore.getOrDefault(userId, new CopyOnWriteArrayList<>());
    }

    @Override
    public List<String> findUserIdsByRoleId(Long roleId) {
        // 单表查询模拟：遍历 user_role 关联关系
        return userRoleStore.entrySet().stream()
                .filter(e -> e.getValue().contains(roleId))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        rolePermStore.computeIfAbsent(roleId, k -> new CopyOnWriteArrayList<>()).add(permissionId);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        rolePermStore.getOrDefault(roleId, new CopyOnWriteArrayList<>()).remove(permissionId);
    }
}
