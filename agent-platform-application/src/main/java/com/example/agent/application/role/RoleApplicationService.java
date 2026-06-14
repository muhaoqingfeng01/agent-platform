package com.example.agent.application.role;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色应用服务 — 编排角色 CRUD、角色-用户分配、角色-权限分配.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;

    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        log.info("[Role] 创建: roleCode={}, tenantId={}", request.getRoleCode(), request.getTenantId());
        Role role = Role.builder()
                .tenantId(request.getTenantId()).roleCode(request.getRoleCode())
                .roleName(request.getRoleName()).description(request.getDescription())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).deleted(false)
                .build();
        roleRepository.save(role);
        return RoleResponse.from(role);
    }

    public List<RoleResponse> listRoles(String tenantId) {
        log.debug("[Role] 列表: tenantId={}", tenantId);
        return roleRepository.findByTenant(tenantId).stream()
                .map(RoleResponse::from).toList();
    }

    @Transactional
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "角色不存在: " + id));
        role = Role.builder()
                .id(role.getId()).tenantId(role.getTenantId()).roleCode(role.getRoleCode())
                .roleName(request.getRoleName())
                .description(request.getDescription() != null ? request.getDescription() : role.getDescription())
                .createdAt(role.getCreatedAt()).updatedAt(LocalDateTime.now()).deleted(role.getDeleted())
                .build();
        roleRepository.update(role);
        return RoleResponse.from(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (roleRepository.findById(id).isEmpty()) {
            throw new BusinessException(404, "角色不存在: " + id);
        }
        roleRepository.delete(id);
    }

    @Transactional
    public void assignRoleToUser(Long roleId, AssignRoleToUserRequest request) {
        log.info("[Role] 分配角色给用户: roleId={}, userId={}", roleId, request.getUserId());
        if (roleRepository.findById(roleId).isEmpty()) {
            throw new BusinessException(404, "角色不存在: " + roleId);
        }
        roleRepository.assignRoleToUser(request.getUserId(), roleId);
    }

    public List<String> getUsersByRole(Long roleId) {
        if (roleRepository.findById(roleId).isEmpty()) {
            throw new BusinessException(404, "角色不存在: " + roleId);
        }
        return roleRepository.findUserIdsByRoleId(roleId);
    }

    @Transactional
    public void assignPermissionToRole(Long roleId, AssignPermissionToRoleRequest request) {
        log.info("[Role] 分配权限给角色: roleId={}, permissionId={}", roleId, request.getPermissionId());
        if (roleRepository.findById(roleId).isEmpty()) {
            throw new BusinessException(404, "角色不存在: " + roleId);
        }
        roleRepository.assignPermissionToRole(roleId, request.getPermissionId());
    }
}
