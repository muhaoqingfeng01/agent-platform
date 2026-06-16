package com.example.agent.application.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 注入攻击过滤器 — order=1，最先执行.
 *
 * <p>检测 SQL 注入和提示词注入（Prompt Injection）特征。
 * <p>使用预编译正则表达式提高性能。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class InjectionFilter implements InputFilter {

    @Override
    public int order() {
        return 1;
    }

    /** 预编译的正则模式 — SQL 注入特征 */
    private static final Pattern[] SQL_PATTERNS = {
            Pattern.compile("(?i)(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER)\\b.*\\b(FROM|INTO|TABLE)\\b)"),
            Pattern.compile("(?i)(--|#|/\\*|\\*/)"),
            Pattern.compile("(?i)(\\bOR\\b.*=.*\\bOR\\b)"),
            Pattern.compile("(?i)(\\bEXEC\\b.*\\bSP_\\b)"),
            Pattern.compile("(?i)(\\bWAITFOR\\b\\s+\\bDELAY\\b)"),
            Pattern.compile("(?i)(\\bSLEEP\\s*\\(\\s*\\d+\\s*\\))")
    };

    /** 预编译的正则模式 — 提示词注入特征 */
    private static final Pattern[] PROMPT_INJECTION_PATTERNS = {
            Pattern.compile("(?i)(ignore\\s+(all\\s+)?(previous|above)\\s+(instructions|prompts?))"),
            Pattern.compile("(?i)(you\\s+(are|must|should|will)\\s+now\\s+(act|behave|pretend))"),
            Pattern.compile("(?i)(system\\s*prompt\\s*(:|=|is|was))"),
            Pattern.compile("(?i)(DAN\\s*(mode|prompt)?)"),
            Pattern.compile("(?i)(developer\\s*mode|dev\\s*mode)"),
            Pattern.compile("(?i)(override\\s+(the\\s+)?(system|instructions|prompt))"),
            Pattern.compile("(?i)(new\\s+instructions?\\s*(:|=))")
    };

    @Override
    public FilterResult filter(String content, FilterContext context) {
        if (content == null || content.isEmpty()) {
            return FilterResult.pass();
        }

        // 1. SQL 注入检测
        for (Pattern pattern : SQL_PATTERNS) {
            if (pattern.matcher(content).find()) {
                log.warn("[InjectionFilter] SQL 注入特征检测: conversationId={}", context.getConversationId());
                return FilterResult.block("INJECTION",
                        "输入内容包含不安全的字符组合，已被安全系统拦截",
                        "SQL injection pattern: " + pattern.pattern());
            }
        }

        // 2. 提示词注入检测
        for (Pattern pattern : PROMPT_INJECTION_PATTERNS) {
            if (pattern.matcher(content).find()) {
                log.warn("[InjectionFilter] 提示词注入特征检测: conversationId={}", context.getConversationId());
                return FilterResult.block("INJECTION",
                        "输入内容包含不安全的指令，已被安全系统拦截",
                        "Prompt injection pattern: " + pattern.pattern());
            }
        }

        return FilterResult.pass();
    }
}
