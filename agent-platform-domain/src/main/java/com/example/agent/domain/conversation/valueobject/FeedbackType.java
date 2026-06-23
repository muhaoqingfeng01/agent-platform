package com.example.agent.domain.conversation.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息反馈类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum FeedbackType {
    LIKE("LIKE", "点赞"),
    DISLIKE("DISLIKE", "点踩");

    private final String code;
    private final String desc;

    @JsonValue
    public String getValue() {
        return code;
    }

    /**
     * 大小写不敏感地反序列化枚举值.
     *
     * @param value 前端传入的字符串（如 "like"、"LIKE"、"dislike"）
     * @return 对应的枚举常量
     */
    @JsonCreator
    public static FeedbackType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (FeedbackType e : values()) {
            if (e.code.equalsIgnoreCase(value)) return e;
        }
        return null;
    }

    public static FeedbackType fromCode(String code) {
        if (code == null || code.isBlank()) return null;
        for (FeedbackType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
