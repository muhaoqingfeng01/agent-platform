package com.example.agent.application.permission;

import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限应用服务 — 编排权限定义 CRUD.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionApplicationService {

    private final PermissionRepository permissionRepository;

    public List<PermissionResponse> listPermissions() {
        log.debug("[Permission] 列表");
        return permissionRepository.findAll().stream()
                .map(PermissionResponse::from).toList();
    }

    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        log.info("[Permission] 创建: permissionCode={}", request.getPermissionCode());
        Permission permission = Permission.builder()
                .permissionCode(request.getPermissionCode())
                .resource(request.getResource()).action(request.getAction())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now()).deleted(false)
                .build();
        permissionRepository.save(permission);
        return PermissionResponse.from(permission);
    }
}
