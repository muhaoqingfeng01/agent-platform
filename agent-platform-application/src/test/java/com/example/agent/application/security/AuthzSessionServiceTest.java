package com.example.agent.application.security;

import com.example.agent.domain.security.PermissionService;
import com.example.agent.domain.security.RoleService;
import com.example.agent.domain.tenant.RoleRepository;
import com.example.agent.infrastructure.security.AuthzCacheStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuthzSessionService 刷新编排测试.
 */
@ExtendWith(MockitoExtension.class)
class AuthzSessionServiceTest {

    @Mock
    private AuthzCacheStore authzCacheStore;
    @Mock
    private PermissionService permissionService;
    @Mock
    private RoleService roleService;
    @Mock
    private RoleRepository roleRepository;

    private AuthzSessionService service;

    @BeforeEach
    void setUp() {
        service = new AuthzSessionService(authzCacheStore, permissionService, roleService, roleRepository);
    }

    @Test
    void onLogin_marksOnlineAndRefreshes() {
        service.onLogin("u1");
        verify(authzCacheStore).markOnline("u1");
        verify(permissionService).refreshPermissionCodes("u1");
        verify(roleService).refreshRoleCodes("u1");
    }

    @Test
    void onLogout_clearsCache() {
        service.onLogout("u1");
        verify(authzCacheStore).clearUser("u1");
    }

    @Test
    void refreshUsersOfRole_refreshesEachUser() {
        when(roleRepository.findUserIdsByRoleId(9L)).thenReturn(List.of("u1", "u2"));
        service.refreshUsersOfRole(9L);
        verify(permissionService).refreshPermissionCodes("u1");
        verify(permissionService).refreshPermissionCodes("u2");
        verify(roleService).refreshRoleCodes("u1");
        verify(roleService).refreshRoleCodes("u2");
    }
}
