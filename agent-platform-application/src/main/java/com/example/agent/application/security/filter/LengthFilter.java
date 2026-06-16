package com.example.agent.application.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 输入长度过滤器 — order=4，最后一道防线.
 *
 * <p>防止超长输入导致 LLM 调用成本失控或拒绝服务。
 * <p>默认限制 10000 字符，可通过 {@code security.filter.max-input-length} 配置。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class LengthFilter implements InputFilter {

    /** 最大输入长度 */
    @Value("${security.filter.max-input-length:10000}")
    private int maxInputLength;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public FilterResult filter(String content, FilterContext context) {
        if (content == null || content.isEmpty()) {
            return FilterResult.pass();
        }

        if (content.length() > maxInputLength) {
            log.warn("[LengthFilter] 输入超长: length={}, max={}, conversationId={}",
                    content.length(), maxInputLength, context.getConversationId());
            return FilterResult.block("LENGTH_EXCEEDED",
                    "输入内容超过最大长度限制（" + maxInputLength + " 字符），请精简后重试",
                    "length=" + content.length() + ", max=" + maxInputLength);
        }

        return FilterResult.pass();
    }
}
