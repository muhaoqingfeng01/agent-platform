package com.example.agent.application.security;

import com.example.agent.domain.security.AuthenticationProvider;
import com.example.agent.domain.security.UserView;
import com.example.agent.domain.security.valueobject.AuthProviderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 认证提供者策略工厂 — Strategy Registry 模式.
 *
 * <p>自动发现所有 AuthenticationProvider 实现并建立类型映射.
 * 根据请求中的 provider 参数路由到对应的认证方式.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
public class AuthProviderFactory {

    private final Map<AuthProviderType, AuthenticationProvider> registry = new EnumMap<>(AuthProviderType.class);

    /** 默认本地认证提供者 */
    private AuthenticationProvider defaultProvider;

    public AuthProviderFactory(List<AuthenticationProvider> providers) {
        for (AuthenticationProvider provider : providers) {
            AuthProviderType type = provider.supportedType();
            if (!registry.containsKey(type)) {
                registry.put(type, provider);
                log.info("[AuthFactory] 注册认证提供者: type={}, impl={}",
                    type, provider.getClass().getSimpleName());
            }
        }
        defaultProvider = registry.get(AuthProviderType.LOCAL);
        log.info("[AuthFactory] 初始化完成，已注册 {} 个认证提供者，默认={}",
            registry.size(), defaultProvider != null ? defaultProvider.getClass().getSimpleName() : "NONE");
    }

    /**
     * 根据类型获取认证提供者.
     */
    public Optional<AuthenticationProvider> get(AuthProviderType type) {
        return Optional.ofNullable(registry.get(type));
    }

    /**
     * 执行认证 — 根据 provider 参数路由.
     *
     * @param providerCode 认证提供者代码（LOCAL/LDAP/SSO）
     * @param username     用户名
     * @param password     密码
     * @return 认证成功返回 UserView
     */
    public UserView authenticate(String providerCode, String username, String password) {
        AuthProviderType type = AuthProviderType.fromCode(providerCode);
        AuthenticationProvider provider = registry.get(type);

        if (provider == null) {
            log.warn("[AuthFactory] 未找到认证提供者: type={}, 使用默认", type);
            provider = defaultProvider;
        }

        if (provider == null) {
            throw new IllegalStateException("无可用认证提供者");
        }

        log.debug("[AuthFactory] 路由认证: type={}, provider={}", type, provider.getClass().getSimpleName());
        return provider.authenticate(username, password);
    }
}
