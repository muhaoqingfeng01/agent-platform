package com.example.agent.infrastructure.security;

import com.example.agent.domain.security.RoleService;
import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现 — 基于数据库查询 t_role + t_user_role
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<String> getRoleCodes(String userId) {
        log.debug("[RoleService] 查询角色: userId={}", userId);

        List<Role> roles = roleRepository.findByUserId(userId);
        List<String> roleCodes = roles.stream()
                .map(Role::getRoleCode)
                .toList();

        log.debug("[RoleService] 角色查询结果: userId={}, roles={}", userId, roleCodes);
        return roleCodes;
    }
}
