package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import com.example.agent.infrastructure.persistence.mapper.RoleMapper;
import com.example.agent.infrastructure.persistence.po.RolePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓储 MyBatis 实现 — 替换 {@code RoleRepositoryStub}
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findById(Long id) {
        return roleMapper.findById(id).map(this::toDomain);
    }

    @Override
    public List<Role> findByTenant(String tenantId) {
        return roleMapper.findByTenant(tenantId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Role> findByUserId(String userId) {
        return roleMapper.findByUserId(userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void save(Role role) {
        roleMapper.insert(toPO(role));
    }

    @Override
    public void update(Role role) {
        roleMapper.update(toPO(role));
    }

    @Override
    public void delete(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public void assignRoleToUser(String userId, Long roleId) {
        roleMapper.insertUserRole(userId, roleId);
    }

    @Override
    public void removeRoleFromUser(String userId, Long roleId) {
        roleMapper.deleteUserRole(userId, roleId);
    }

    @Override
    public List<Long> findRoleIdsByUserId(String userId) {
        return roleMapper.findRoleIdsByUserId(userId);
    }

    @Override
    public List<String> findUserIdsByRoleId(Long roleId) {
        return roleMapper.findUserIdsByRoleId(roleId);
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        roleMapper.insertRolePermission(roleId, permissionId);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        roleMapper.deleteRolePermission(roleId, permissionId);
    }

    // ==================== 映射方法 ====================

    private Role toDomain(RolePO po) {
        return Role.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .roleCode(po.getRoleCode())
                .roleName(po.getRoleName())
                .description(po.getDescription())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .deleted(po.getDeleted())
                .build();
    }

    private RolePO toPO(Role role) {
        return RolePO.builder()
                .id(role.getId())
                .tenantId(role.getTenantId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .deleted(role.getDeleted())
                .build();
    }
}
