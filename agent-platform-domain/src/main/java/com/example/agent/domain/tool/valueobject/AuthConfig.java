package com.example.agent.domain.tool.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 认证配置值对象 — 存储工具调用时所需的认证信息.
 *
 * <p>认证方式见 {@link AuthType}。
 * headers 字段用于存放额外的自定义请求头，如 X-API-Version 等.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthConfig {

    /** 认证类型 */
    private AuthType authType;

    /** API 密钥 — {@link AuthType#API_KEY} 时使用 */
    private String apiKey;

    /** Bearer Token — {@link AuthType#BEARER} 时使用 */
    private String token;

    /** 用户名 — {@link AuthType#BASIC} 时使用 */
    private String username;

    /** 密码 — {@link AuthType#BASIC} 时使用 */
    private String password;

    /** 额外的自定义请求头 — 如 {"X-API-Version": "v2", "X-Client-Id": "agent-platform"} */
    private Map<String, String> headers;
}
