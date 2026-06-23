package com.example.agent.domain.security.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感词分类.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum SensitiveCategory {
    /** SQL/提示词注入特征 */
    INJECTION("INJECTION", "注入攻击"),
    /** 越狱提示词（DAN/ignore previous） */
    JAILBREAK("JAILBREAK", "越狱检测"),
    /** 个人身份信息（身份证/手机/邮箱） */
    PII("PII", "个人隐私"),
    /** 自定义敏感词 */
    CUSTOM("CUSTOM", "自定义");

    private final String code;
    private final String desc;

    public static SensitiveCategory fromCode(String code) {
        if (code == null || code.isBlank()) return CUSTOM;
        for (SensitiveCategory e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return CUSTOM;
    }
}
