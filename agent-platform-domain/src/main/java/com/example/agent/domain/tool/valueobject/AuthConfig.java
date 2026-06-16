package com.example.agent.domain.tool.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 认证配置值对象 — 存储工具调用时所需的认证信息.
 *
 * <p>支持四种认证方式: API_KEY（API 密钥）、BEARER（Bearer Token）、
 * BASIC（HTTP Basic Auth）、NONE（无需认证）.
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

    /** 认证类型 — API_KEY / BEARER / BASIC / NONE */
    private String authType;

    /** API 密钥 — authType=API_KEY 时使用，放入 Authorization 或自定义 Header */
    private String apiKey;

    /** Bearer Token — authType=BEARER 时使用 */
    private String token;

    /** 用户名 — authType=BASIC 时使用 */
    private String username;

    /** 密码 — authType=BASIC 时使用 */
    private String password;

    /** 额外的自定义请求头 — 如 {"X-API-Version": "v2", "X-Client-Id": "agent-platform"} */
    private Map<String, String> headers;
}
