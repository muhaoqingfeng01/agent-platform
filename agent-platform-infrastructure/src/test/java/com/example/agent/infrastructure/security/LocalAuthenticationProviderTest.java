package com.example.agent.infrastructure.security;

import com.example.agent.common.exception.AuthenticationException;
import com.example.agent.domain.security.UserService;
import com.example.agent.domain.security.UserView;
import com.example.agent.domain.security.valueobject.AuthProviderType;
import com.example.agent.domain.tenant.valueobject.UserStatusEnums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalAuthenticationProviderTest {

    @Mock
    private UserService userService;

    private LocalAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        provider = new LocalAuthenticationProvider(userService);
    }

    @Test
    void supportedType_isLocal() {
        assertEquals(AuthProviderType.LOCAL, provider.supportedType());
    }

    @Test
    void authenticate_success() {
        UserView view = new UserView();
        view.setUserId("u1");
        view.setUsername("admin");
        view.setTenantId(1L);
        view.setStatus(UserStatusEnums.ACTIVE);
        when(userService.authenticate(1L, "admin", "pwd")).thenReturn(view);

        UserView result = provider.authenticate(1L, "admin", "pwd");
        assertEquals("u1", result.getUserId());
    }

    @Test
    void authenticate_nullUser_throws() {
        when(userService.authenticate(1L, "admin", "bad")).thenReturn(null);
        assertThrows(AuthenticationException.class,
                () -> provider.authenticate(1L, "admin", "bad"));
    }
}
