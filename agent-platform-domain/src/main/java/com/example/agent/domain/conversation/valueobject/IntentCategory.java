package com.example.agent.domain.conversation.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 意图分类枚举.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum IntentCategory {
    FAQ("常见问答"),
    TASK("任务执行"),
    CHITCHAT("闲聊"),
    MULTI_STEP("多步骤任务");

    private final String label;
}
