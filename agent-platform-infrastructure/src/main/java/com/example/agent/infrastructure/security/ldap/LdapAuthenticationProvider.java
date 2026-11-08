package com.example.agent.infrastructure.security.ldap;

import com.example.agent.common.exception.AuthenticationException;
import com.example.agent.domain.security.AuthenticationProvider;
import com.example.agent.domain.security.UserView;
import com.example.agent.domain.security.valueobject.AuthProviderType;
import com.example.agent.domain.tenant.User;
import com.example.agent.domain.tenant.UserRepository;
import com.example.agent.infrastructure.config.security.LdapConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javax.naming.directory.DirContext;

/**
 * LDAP 认证提供者 — 企业目录 bind 校验后映射本地用户.
 *
 * <p>条件装配: {@code spring.ldap.enabled=true}.
 * <p>流程: LDAP bind 成功 → 按 tenantId+username 查本地账号 → 返回 UserView.
 * 本地无账号时抛错，不做本地密码降级、不自动建号.
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
    private final LdapContextSource ldapContextSource;
    private final UserRepository userRepository;

    @Override
    public AuthProviderType supportedType() {
        return AuthProviderType.LDAP;
    }

    @Override
    public UserView authenticate(Long tenantId, String username, String password) {
        log.info("[LDAP] 认证请求: username={}, ldapUrl={}, tenantId={}",
                username, ldapConfig.getUrls(), tenantId);

        if (username == null || username.isBlank() || password == null) {
            throw new AuthenticationException("LDAP 认证失败: 用户名或密码为空");
        }

        if (!bind(username, password)) {
            throw new AuthenticationException("LDAP 认证失败: " + username);
        }

        if (tenantId == null) {
            throw new AuthenticationException("LDAP 认证成功但缺少 tenantId，无法定位本地账号");
        }

        User user = userRepository.findByTenantAndUsername(tenantId, username).orElse(null);
        if (user == null) {
            throw new AuthenticationException("LDAP 认证成功但未开通本地账号: " + username);
        }
        if (!user.isActive()) {
            throw new AuthenticationException("账户已停用，请联系管理员");
        }

        log.info("[LDAP] 认证成功并映射本地用户: userId={}, tenantId={}",
                user.getUserId(), user.getTenantId());
        return user.toView();
    }

    /**
     * 按 userDnPattern 构造用户 DN 并尝试 bind.
     */
    private boolean bind(String username, String password) {
        String userDn = resolveUserDn(username);
        DirContext ctx = null;
        try {
            ctx = ldapContextSource.getContext(userDn, password);
            return true;
        } catch (Exception e) {
            log.warn("[LDAP] bind 校验失败: username={}, dn={}, error={}",
                    username, userDn, e.getMessage());
            return false;
        } finally {
            LdapUtils.closeContext(ctx);
        }
    }

    /**
     * 将 {@code uid={0},ou=users} 与 base DN 拼成完整用户 DN.
     */
    private String resolveUserDn(String username) {
        String pattern = ldapConfig.getUserDnPattern();
        if (pattern == null || pattern.isBlank()) {
            pattern = "uid={0}";
        }
        String relative = pattern.replace("{0}", username);
        String base = ldapConfig.getBase();
        if (base == null || base.isBlank()) {
            return relative;
        }
        if (relative.toLowerCase().contains(base.toLowerCase())) {
            return relative;
        }
        return relative + "," + base;
    }
}
