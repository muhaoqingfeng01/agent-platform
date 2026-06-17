package com.example.agent.domain.security;

import com.example.agent.domain.security.valueobject.AuthProviderType;

/**
 * 认证提供者接口 — Strategy 模式.
 *
 * <p>不同认证方式（本地/LDAP/SSO）实现此接口，
 * 由 AuthProviderFactory 根据请求参数路由.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
public interface AuthenticationProvider {

    /** 支持的认证类型 */
    AuthProviderType supportedType();

    /**
     * 执行认证.
     *
     * @param username 用户名
     * @param password 密码
     * @return 认证成功返回 UserView，失败抛出异常
     */
    UserView authenticate(String username, String password);
}
