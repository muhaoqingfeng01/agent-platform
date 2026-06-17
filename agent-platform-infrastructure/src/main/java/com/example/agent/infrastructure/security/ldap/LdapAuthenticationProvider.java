package com.example.agent.infrastructure.security.ldap;

import com.example.agent.common.exception.AuthenticationException;
import com.example.agent.domain.security.AuthenticationProvider;
import com.example.agent.domain.security.UserService;
import com.example.agent.domain.security.UserView;
import com.example.agent.domain.security.valueobject.AuthProviderType;
import com.example.agent.infrastructure.config.security.LdapConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * LDAP 认证提供者 — 企业目录服务认证.
 *
 * <p>条件装配: spring.ldap.enabled=true 时启用.
 *
 * <p>当前为 STUB 实现 — 使用本地 UserService 验证作为降级方案，
 * 生产环境需替换为真实的 LDAP 绑定验证.
 *
 * <p>正式实现流程:
 * <ol>
 *   <li>LDAP 搜索用户 DN: filter=uid={username}, base=ou=users,dc=company,dc=com</li>
 *   <li>绑定验证: ldapTemplate.authenticate(baseDN, filter, password)</li>
 *   <li>属性映射: LDAP attributes → 本地 UserView</li>
 * </ol>
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ldap.enabled", havingValue = "true")
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private final LdapConfig ldapConfig;
    private final UserService userService;   // 降级方案

    @Override
    public AuthProviderType supportedType() {
        return AuthProviderType.LDAP;
    }

    @Override
    public UserView authenticate(String username, String password) {
        log.info("[LDAP] 认证请求: username={}, ldapUrl={}", username, ldapConfig.getUrls());

        // TODO: 替换为真实 LDAP 绑定验证
        // 1. 通过 LdapTemplate 搜索用户 DN
        // String userDn = searchUserDn(username);
        // 2. 绑定验证
        // boolean authenticated = ldapTemplate.authenticate(
        //     LdapQueryBuilder.query().base(ldapConfig.getBase())
        //         .filter(ldapConfig.getUserDnPattern().replace("{0}", username)),
        //     password);
        // 3. 属性映射到 UserView
        // if (authenticated) { return mapLdapAttributes(username); }

        // STUB: 降级到本地认证
        log.warn("[LDAP] LDAP 服务器不可用，降级到本地认证: username={}", username);
        UserView user = userService.authenticate("ldap", username, password);
        if (user == null || !user.isActive()) {
            throw new AuthenticationException("LDAP/本地认证失败: " + username);
        }
        return user;
    }
}
