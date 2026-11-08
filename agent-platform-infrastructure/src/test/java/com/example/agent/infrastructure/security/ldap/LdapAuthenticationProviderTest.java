package com.example.agent.infrastructure.security.ldap;

import com.example.agent.common.exception.AuthenticationException;
import com.example.agent.domain.tenant.User;
import com.example.agent.domain.tenant.UserRepository;
import com.example.agent.domain.tenant.valueobject.UserStatusEnums;
import com.example.agent.infrastructure.config.security.LdapConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.naming.directory.DirContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LdapAuthenticationProviderTest {

    @Mock
    private LdapContextSource ldapContextSource;
    @Mock
    private UserRepository userRepository;

    private LdapConfig ldapConfig;
    private LdapAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        ldapConfig = new LdapConfig();
        ldapConfig.setUrls("ldap://localhost:389");
        ldapConfig.setBase("dc=company,dc=com");
        ldapConfig.setUserDnPattern("uid={0},ou=users");
        provider = new LdapAuthenticationProvider(ldapConfig, ldapContextSource, userRepository);
    }

    @Test
    void authenticate_bindOkAndLocalUserExists() throws Exception {
        DirContext ctx = mock(DirContext.class);
        when(ldapContextSource.getContext(eq("uid=alice,ou=users,dc=company,dc=com"), eq("secret")))
                .thenReturn(ctx);

        User user = User.builder()
                .userId("u-ldap")
                .username("alice")
                .tenantId(1L)
                .status(UserStatusEnums.ACTIVE)
                .build();
        when(userRepository.findByTenantAndUsername(1L, "alice")).thenReturn(Optional.of(user));

        assertEquals("u-ldap", provider.authenticate(1L, "alice", "secret").getUserId());
    }

    @Test
    void authenticate_bindOkButNoLocalUser_throws() throws Exception {
        DirContext ctx = mock(DirContext.class);
        when(ldapContextSource.getContext(anyString(), anyString())).thenReturn(ctx);
        when(userRepository.findByTenantAndUsername(1L, "alice")).thenReturn(Optional.empty());

        AuthenticationException ex = assertThrows(AuthenticationException.class,
                () -> provider.authenticate(1L, "alice", "secret"));
        assertEquals(true, ex.getMessage().contains("未开通本地账号"));
    }

    @Test
    void authenticate_bindFail_throws() {
        when(ldapContextSource.getContext(anyString(), anyString()))
                .thenThrow(new RuntimeException("invalid credentials"));

        assertThrows(AuthenticationException.class,
                () -> provider.authenticate(1L, "alice", "bad"));
    }
}
