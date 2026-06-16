package com.example.agent.domain.security.valueobject;

/**
 * 敏感词分类.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum SensitiveCategory {
    /** SQL/提示词注入特征 */
    INJECTION,
    /** 越狱提示词（DAN/ignore previous） */
    JAILBREAK,
    /** 个人身份信息（身份证/手机/邮箱） */
    PII,
    /** 自定义敏感词 */
    CUSTOM;

    public static SensitiveCategory fromCode(String code) {
        if (code == null) return CUSTOM;
        try {
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CUSTOM;
        }
    }
}
