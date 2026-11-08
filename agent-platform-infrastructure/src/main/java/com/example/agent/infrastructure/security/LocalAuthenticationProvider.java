package com.example.agent.infrastructure.security;

import com.example.agent.common.exception.AuthenticationException;
import com.example.agent.domain.security.AuthenticationProvider;
import com.example.agent.domain.security.UserService;
import com.example.agent.domain.security.UserView;
import com.example.agent.domain.security.valueobject.AuthProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 本地数据库认证提供者 — 默认认证方式.
 *
 * <p>委托 {@link UserService} 校验租户内用户名密码.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    @Override
    public AuthProviderType supportedType() {
        return AuthProviderType.LOCAL;
    }

    @Override
    public UserView authenticate(Long tenantId, String username, String password) {
        log.debug("[LOCAL] 认证请求: tenantId={}, username={}", tenantId, username);
        UserView user = userService.authenticate(tenantId, username, password);
        if (user == null) {
            throw new AuthenticationException("用户名或密码错误");
        }
        if (!user.isActive()) {
            throw new AuthenticationException("账户已停用，请联系管理员");
        }
        return user;
    }
}
