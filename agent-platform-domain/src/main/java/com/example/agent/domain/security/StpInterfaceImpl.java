package com.example.agent.domain.security;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限加载器实现
 * <p>
 * 实现 {@link StpInterface}，每次鉴权时自动调用，
 * 从数据库加载当前登录用户的权限码和角色码。
 * <p>
 * 调用时机：
 * <ul>
 *   <li>{@code @SaCheckPermission("xxx")} 注解触发时，调用 {@link #getPermissionList}</li>
 *   <li>{@code @SaCheckRole("xxx")} 注解触发时，调用 {@link #getRoleList}</li>
 * </ul>
 *
 * @see StpInterface
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final PermissionService permissionService;
    private final RoleService roleService;

    /**
     * 获取当前登录用户的权限编码列表
     * <p>
     * 从数据库查询用户所有角色关联的权限，用于 @SaCheckPermission 鉴权。
     *
     * @param loginId   登录 ID（即 {@code user.getUserId()}）
     * @param loginType 登录类型（如 login、admin 等，默认 "login"）
     * @return 权限编码集合，如 ["tenant:read", "user:write"]
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginId == null) {
            log.warn("[Sa-Token] getPermissionList 被调用但 loginId 为 null");
            return Collections.emptyList();
        }

        String userId = loginId.toString();
        log.debug("[Sa-Token] 加载用户权限: userId={}, loginType={}", userId, loginType);

        try {
            List<String> permissions = permissionService.getPermissionCodes(userId);
            log.debug("[Sa-Token] 权限加载完成: userId={}, permissionCount={}", userId, permissions.size());
            return permissions;
        } catch (Exception e) {
            log.error("[Sa-Token] 权限加载失败: userId={}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取当前登录用户的角色编码列表
     * <p>
     * 从数据库查询用户关联的角色，用于 @SaCheckRole 鉴权。
     *
     * @param loginId   登录 ID（即 {@code user.getUserId()}）
     * @param loginType 登录类型
     * @return 角色编码集合，如 ["ADMIN", "OPERATOR"]
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            log.warn("[Sa-Token] getRoleList 被调用但 loginId 为 null");
            return Collections.emptyList();
        }

        String userId = loginId.toString();
        log.debug("[Sa-Token] 加载用户角色: userId={}, loginType={}", userId, loginType);

        try {
            List<String> roles = roleService.getRoleCodes(userId);
            log.debug("[Sa-Token] 角色加载完成: userId={}, roles={}", userId, roles);
            return roles;
        } catch (Exception e) {
            log.error("[Sa-Token] 角色加载失败: userId={}", userId, e);
            return Collections.emptyList();
        }
    }
}
