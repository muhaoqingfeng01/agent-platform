package com.example.agent.infrastructure.config.security;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LDAP 配置属性 — 企业目录服务连接参数.
 *
 * <p>条件装配: spring.ldap.enabled=true 时启用.
 * <p>开发环境可通过 OpenLDAP Docker 搭建本地测试环境.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Data
@Configuration
@ConditionalOnProperty(name = "spring.ldap.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "spring.ldap")
public class LdapConfig {

    /** LDAP 服务器 URL（如 ldap://ldap.company.com:389） */
    private String urls = "ldap://localhost:389";

    /** LDAP Base DN（如 dc=company,dc=com） */
    private String base = "dc=company,dc=com";

    /** 用户 DN 模式（如 uid={0},ou=users） */
    private String userDnPattern = "uid={0},ou=users";

    /** 绑定时使用的管理员 DN（可选） */
    private String managerDn;

    /** 管理员密码（可选） */
    private String managerPassword;
}
