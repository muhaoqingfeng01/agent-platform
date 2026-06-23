package com.example.agent.domain.security.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证提供者类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum AuthProviderType {
    LOCAL("LOCAL", "本地认证"),     // 本地数据库认证（默认）
    LDAP("LDAP", "企业LDAP"),       // 企业 LDAP 认证
    SSO("SSO", "单点登录");         // OAuth2/OIDC 单点登录

    private final String code;
    private final String desc;

    public static AuthProviderType fromCode(String code) {
        if (code == null || code.isBlank()) return LOCAL;
        for (AuthProviderType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return LOCAL;
    }
}
