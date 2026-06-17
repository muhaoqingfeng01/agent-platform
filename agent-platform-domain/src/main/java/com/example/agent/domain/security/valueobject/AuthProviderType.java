package com.example.agent.domain.security.valueobject;

/**
 * 认证提供者类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
public enum AuthProviderType {
    LOCAL,    // 本地数据库认证（默认）
    LDAP,     // 企业 LDAP 认证
    SSO;      // OAuth2/OIDC 单点登录

    public static AuthProviderType fromCode(String code) {
        if (code == null || code.isBlank()) return LOCAL;
        for (AuthProviderType t : values()) {
            if (t.name().equalsIgnoreCase(code)) return t;
        }
        return LOCAL;
    }
}
