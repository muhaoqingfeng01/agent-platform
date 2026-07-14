package com.example.agent.application.security.filter;

import com.example.agent.infrastructure.config.nacos.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 输入长度过滤器 — order=4，最后一道防线.
 *
 * <p>防止超长输入导致 LLM 调用成本失控或拒绝服务。
 * <p>默认限制 10000 字符，可通过 Nacos {@code agent-platform-security.json} 动态调整。
 * <p>🆕 P6 配置治理子方案03: @Value 替换为 SecurityConfig Nacos 动态配置.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class LengthFilter implements InputFilter {

    private final SecurityConfig securityConfig;

    public LengthFilter(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public int order() {
        return 4;
    }

    @Override
    public FilterResult filter(String content, FilterContext context) {
        if (content == null || content.isEmpty()) {
            return FilterResult.pass();
        }

        int maxInputLength = securityConfig.getMaxInputLength();
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
