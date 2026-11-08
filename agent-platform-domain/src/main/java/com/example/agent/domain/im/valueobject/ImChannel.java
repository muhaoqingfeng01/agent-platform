package com.example.agent.domain.im.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * IM 渠道枚举。
 */
@Getter
@AllArgsConstructor
public enum ImChannel {

    WECOM("WECOM", "企业微信"),
    DINGTALK("DINGTALK", "钉钉");

    private final String code;
    private final String desc;

    public static ImChannel fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("IM 渠道不能为空");
        }
        for (ImChannel channel : values()) {
            if (channel.code.equalsIgnoreCase(code)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("不支持的 IM 渠道: " + code);
    }
}
