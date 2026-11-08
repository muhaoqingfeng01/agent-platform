package com.example.agent.application.security;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.domain.security.PermissionService;
import com.example.agent.domain.security.RoleService;
import com.example.agent.domain.tenant.RoleRepository;
import com.example.agent.infrastructure.security.AuthzCacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 鉴权会话编排 — 登录预热缓存、登出清理、权限变更动态刷新、定时刷新在线用户.
 * <p>
 * 权限变更时只刷新缓存，不强制退出登录。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthzSessionService {

    private final AuthzCacheStore authzCacheStore;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final RoleRepository roleRepository;

    /** 登录成功：标记在线并写入最新权限/角色缓存 */
    public void onLogin(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        authzCacheStore.markOnline(userId);
        permissionService.refreshPermissionCodes(userId);
        roleService.refreshRoleCodes(userId);
        log.info("[AuthzSession] 登录已预热权限缓存: userId={}", userId);
    }

    /** 登出：清除在线标记与权限缓存 */
    public void onLogout(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        authzCacheStore.clearUser(userId);
        log.info("[AuthzSession] 登出已清除权限缓存: userId={}", userId);
    }

    /**
     * 指定用户权限/角色变更后动态刷新缓存（保持登录态）.
     */
    public void refreshUserAuthz(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        permissionService.refreshPermissionCodes(userId);
        roleService.refreshRoleCodes(userId);
        log.info("[AuthzSession] 已动态刷新用户权限缓存: userId={}", userId);
    }

    /**
     * 角色权限或成员变更：刷新该角色下所有用户的缓存.
     */
    public void refreshUsersOfRole(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<String> userIds = roleRepository.findUserIdsByRoleId(roleId);
        for (String userId : userIds) {
            refreshUserAuthz(userId);
        }
        log.info("[AuthzSession] 角色变更已刷新关联用户缓存: roleId={}, userCount={}",
                roleId, userIds.size());
    }

    /**
     * 权限定义变更（如级联删除）：刷新全部在线用户缓存（含通配符展开场景）.
     */
    public void refreshAllOnlineUsers() {
        Set<String> online = new HashSet<>(authzCacheStore.listOnlineUserIds());
        int refreshed = 0;
        for (String userId : online) {
            if (!isStillLoggedIn(userId)) {
                authzCacheStore.clearUser(userId);
                continue;
            }
            refreshUserAuthz(userId);
            refreshed++;
        }
        log.info("[AuthzSession] 已刷新在线用户权限缓存: online={}, refreshed={}", online.size(), refreshed);
    }

    /**
     * 定时任务：对仍处于登录态的用户重载最新权限；已失效登录态则清理.
     */
    public void scheduledRefreshOnlineUsers() {
        Set<String> online = new HashSet<>(authzCacheStore.listOnlineUserIds());
        if (online.isEmpty()) {
            log.debug("[AuthzSession] 定时刷新：无在线用户");
            return;
        }
        int refreshed = 0;
        int cleared = 0;
        for (String userId : online) {
            try {
                if (!isStillLoggedIn(userId)) {
                    authzCacheStore.clearUser(userId);
                    cleared++;
                    continue;
                }
                permissionService.refreshPermissionCodes(userId);
                roleService.refreshRoleCodes(userId);
                refreshed++;
            } catch (Exception e) {
                log.warn("[AuthzSession] 定时刷新失败: userId={}, err={}", userId, e.getMessage());
            }
        }
        log.info("[AuthzSession] 定时刷新完成: scanned={}, refreshed={}, cleared={}",
                online.size(), refreshed, cleared);
    }

    private boolean isStillLoggedIn(String userId) {
        try {
            return StpUtil.isLogin(userId);
        } catch (Exception e) {
            return false;
        }
    }
}
