package com.example.agent.domain.conversation.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 消息反馈类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum FeedbackType {
    LIKE,
    DISLIKE;

    @JsonValue
    public String getValue() {
        return name();
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
        return FeedbackType.valueOf(value.toUpperCase());
    }
}
