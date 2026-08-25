package com.example.agent.domain.tool.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP 工具认证类型 — 对应 {@link AuthConfig#getAuthType()}.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AuthType {

    /** API 密钥 — 写入 Authorization: Bearer {apiKey} */
    API_KEY("API_KEY", "API 密钥"),

    /** Bearer Token — 写入 Authorization: Bearer {token} */
    BEARER("BEARER", "Bearer Token"),

    /** HTTP Basic 认证 — 写入 Authorization: Basic {base64(user:pass)} */
    BASIC("BASIC", "HTTP Basic 认证"),

    /** 无需认证，仅允许自定义请求头 */
    NONE("NONE", "无需认证");

    @JsonValue
    private final String code;
    private final String desc;

    /**
     * 根据代码字符串查找对应的枚举值.
     *
     * @param code 认证类型代码（大小写不敏感）
     * @return 对应的枚举值；空值视为 {@link #NONE}
     * @throws IllegalArgumentException 如果代码不匹配任何已知类型
     */
    @JsonCreator
    public static AuthType fromCode(String code) {
        if (code == null || code.isBlank()) {
            return NONE;
        }
        for (AuthType e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException(
                "未知的认证类型: " + code + "，有效值: API_KEY, BEARER, BASIC, NONE");
    }
}
