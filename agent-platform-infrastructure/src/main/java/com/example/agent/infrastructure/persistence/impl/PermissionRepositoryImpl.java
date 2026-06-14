package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;
import com.example.agent.infrastructure.persistence.mapper.PermissionMapper;
import com.example.agent.infrastructure.persistence.po.PermissionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限仓储 MyBatis 实现 — 替换 {@code PermissionRepositoryStub}
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionMapper.findById(id).map(this::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return permissionMapper.findAll()
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Permission> findByRoleId(Long roleId) {
        return permissionMapper.findByRoleId(roleId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Permission> findByUserId(String userId) {
        return permissionMapper.findByUserId(userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void save(Permission permission) {
        permissionMapper.insert(toPO(permission));
    }

    @Override
    public void delete(Long id) {
        permissionMapper.deleteById(id);
    }

    // ==================== 映射方法 ====================

    private Permission toDomain(PermissionPO po) {
        return Permission.builder()
                .id(po.getId())
                .permissionCode(po.getPermissionCode())
                .resource(po.getResource())
                .action(po.getAction())
                .description(po.getDescription())
                .createdAt(po.getCreatedAt())
                .deleted(po.getDeleted())
                .build();
    }

    private PermissionPO toPO(Permission permission) {
        return PermissionPO.builder()
                .id(permission.getId())
                .permissionCode(permission.getPermissionCode())
                .resource(permission.getResource())
                .action(permission.getAction())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .deleted(permission.getDeleted())
                .build();
    }
}
