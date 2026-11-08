package com.example.agent.infrastructure.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * LDAP 连接与 {@link LdapTemplate} 装配.
 *
 * <p>仅在 {@code spring.ldap.enabled=true} 时生效.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.ldap.enabled", havingValue = "true")
public class LdapTemplateConfig {

    @Bean
    public LdapContextSource ldapContextSource(LdapConfig ldapConfig) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapConfig.getUrls());
        contextSource.setBase(ldapConfig.getBase());
        if (ldapConfig.getManagerDn() != null && !ldapConfig.getManagerDn().isBlank()) {
            contextSource.setUserDn(ldapConfig.getManagerDn());
            contextSource.setPassword(ldapConfig.getManagerPassword());
        }
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource ldapContextSource) {
        return new LdapTemplate(ldapContextSource);
    }
}
