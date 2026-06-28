package com.example.agent.application.permission;

import com.example.agent.common.dto.PageResponse;
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

    /**
     * 分页查询权限列表
     */
    public PageResponse<PermissionResponse> listPermissionsPaginated(int page, int size) {
        log.debug("[Permission] 分页查询: page={}, size={}", page, size);
        List<Permission> permissions = permissionRepository.findAllPaginated(page, size);
        long total = permissionRepository.count();
        List<PermissionResponse> records = permissions.stream()
                .map(PermissionResponse::from).toList();
        return PageResponse.of(records, total, page, size);
    }

    @Transactional
    public PermissionResponse createPermission(PermissionCreateCommand request) {
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

    /**
     * 级联删除权限 — 事务保证原子性
     * <p>
     * 执行顺序：删除 t_role_permission 关联 → 逻辑删除 t_permission
     */
    @Transactional
    public void deletePermissionCascade(Long id) {
        log.info("[Permission] 级联删除: id={}", id);
        permissionRepository.deleteCascade(id);
    }

    /**
     * 批量导入权限 — 事务保证原子性
     * <p>
     * 全部成功或全部回滚；跳过与已有 permission_code 重复的记录（由 DB 唯一索引兜底）。
     */
    @Transactional
    public List<PermissionResponse> batchImport(PermissionBatchImportCommand request) {
        log.info("[Permission] 批量导入: count={}", request.getPermissions().size());
        LocalDateTime now = LocalDateTime.now();
        List<Permission> permissions = request.getPermissions().stream()
                .map(r -> Permission.builder()
                        .permissionCode(r.getPermissionCode())
                        .resource(r.getResource())
                        .action(r.getAction())
                        .description(r.getDescription())
                        .createdAt(now)
                        .deleted(false)
                        .build())
                .toList();
        permissionRepository.saveAll(permissions);
        return permissions.stream().map(PermissionResponse::from).toList();
    }
}
