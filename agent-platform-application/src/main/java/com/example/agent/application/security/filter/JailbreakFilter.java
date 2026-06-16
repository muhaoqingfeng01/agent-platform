package com.example.agent.application.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 越狱检测过滤器 — order=2.
 *
 * <p>检测旨在绕过 AI 安全限制的越狱提示词（Jailbreak）。
 * <p>涵盖 DAN、角色扮演越狱、目标劫持、Token 走私等常见越狱手法。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class JailbreakFilter implements InputFilter {

    @Override
    public int order() {
        return 2;
    }

    /** 越狱提示词特征 — 预编译正则 */
    private static final Pattern[] JAILBREAK_PATTERNS = {
            // DAN 模式
            Pattern.compile("(?i)(DAN\\s*(mode|prompt|is|stands))"),
            // 角色扮演越狱
            Pattern.compile("(?i)(pretend\\s+(you\\s+are|to\\s+be)\\s+(a|an))"),
            Pattern.compile("(?i)(role\\s*play|roleplay)\\s+as"),
            Pattern.compile("(?i)(act\\s+as\\s+(if\\s+)?(a|an)\\b)"),
            // 目标劫持
            Pattern.compile("(?i)(forget\\s+(all\\s+)?(previous|your|the))"),
            Pattern.compile("(?i)(disregard\\s+(all\\s+)?(previous|above|instructions))"),
            Pattern.compile("(?i)(from\\s+now\\s+on\\s+(you\\s+are|your))"),
            Pattern.compile("(?i)(I\\s+(am|have\\s+been)\\s+your\\s+(creator|developer|owner))"),
            // Token 走私 / 编码绕过
            Pattern.compile("(?i)(base64\\s*(decode|encode)?\\s*[:\\{]?\\s*[A-Za-z0-9+/=]{20,})"),
            // 伦理绕过
            Pattern.compile("(?i)(you\\s+(don't|do\\s+not)\\s+(have\\s+to|need\\s+to)\\s+(follow|obey))"),
            Pattern.compile("(?i)(ethical\\s+(guidelines?|constraints?|restrictions?)\\s+(don't|do\\s+not)\\s+apply)"),
            Pattern.compile("(?i)((illegal|unethical|harmful|dangerous)\\s+.*\\s+(just|only)\\s+(this\\s+once|for\\s+research))"),
            // 分步越狱
            Pattern.compile("(?i)(answer\\s+(step\\s+by\\s+step|in\\s+detail)\\s+how\\s+to\\s+(hack|crack|exploit|bypass))")
    };

    @Override
    public FilterResult filter(String content, FilterContext context) {
        if (content == null || content.isEmpty()) {
            return FilterResult.pass();
        }

        for (Pattern pattern : JAILBREAK_PATTERNS) {
            if (pattern.matcher(content).find()) {
                log.warn("[JailbreakFilter] 越狱提示词检测: conversationId={}", context.getConversationId());
                return FilterResult.block("JAILBREAK",
                        "输入内容包含不安全的指令，已被安全系统拦截",
                        "Jailbreak pattern: " + pattern.pattern());
            }
        }

        return FilterResult.pass();
    }
}
