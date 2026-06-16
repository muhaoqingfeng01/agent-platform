package com.example.agent.application.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * PII（个人身份信息）输出脱敏器.
 *
 * <p>在 LLM 响应返回给用户之前，自动检测并脱敏身份证号、手机号、邮箱、银行卡号等敏感信息。
 * <p>使用正则表达式匹配并替换中间部分为星号。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class PiiDesensitizer {

    /**
     * PII 脱敏规则 — 保持插入顺序（LruCache 风格）.
     * Key: PII 类型名称, Value: [Pattern, Replacement]
     */
    private static final Map<String, PiiRule> PII_RULES = new LinkedHashMap<>();

    static {
        // 身份证号: 6位地区 + 8位生日 + 4位校验 → 6位地区****校验
        PII_RULES.put("身份证号", new PiiRule(
                Pattern.compile("(\\b\\d{6})\\d{8}(\\d{4}\\b|\\d{3}[Xx]\\b)"),
                "$1********$2"
        ));
        // 手机号: 1XX + 4位 + 4位 → 1XX****后4位
        PII_RULES.put("手机号", new PiiRule(
                Pattern.compile("(\\b1[3-9]\\d)\\d{4}(\\d{4}\\b)"),
                "$1****$2"
        ));
        // 邮箱: 前2字符 + 中间 + @域名 → 前2***@域名
        PII_RULES.put("邮箱", new PiiRule(
                Pattern.compile("(\\b[A-Za-z0-9._%+-]{2})[A-Za-z0-9._%+-]*(@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b)"),
                "$1***$2"
        ));
        // 银行卡: 前4位 + 中间 + 后4位 → 前4位****后4位
        PII_RULES.put("银行卡", new PiiRule(
                Pattern.compile("(\\b\\d{4})\\d{8,16}(\\d{4}\\b)"),
                "$1****$2"
        ));
        // 固定电话: 区号-号码 → 区号-****
        PII_RULES.put("固定电话", new PiiRule(
                Pattern.compile("(\\b0\\d{2,3}-?)\\d{7,8}\\b"),
                "$1****"
        ));
        // IPv4 地址
        PII_RULES.put("IP地址", new PiiRule(
                Pattern.compile("(\\b\\d{1,3})\\.\\d{1,3}\\.\\d{1,3}\\.(\\d{1,3}\\b)"),
                "$1.*.*.$2"
        ));
    }

    /**
     * 对文本中的 PII 进行脱敏处理.
     *
     * @param text 原始文本（可能包含 PII）
     * @return 脱敏后的文本
     */
    public String desensitize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String result = text;
        for (Map.Entry<String, PiiRule> entry : PII_RULES.entrySet()) {
            PiiRule rule = entry.getValue();
            String before = result;
            result = rule.pattern.matcher(result).replaceAll(rule.replacement);
            if (!result.equals(before)) {
                log.debug("[PiiDesensitizer] 脱敏 {}: length={}", entry.getKey(), text.length());
            }
        }

        return result;
    }

    /**
     * 检测文本中是否包含 PII.
     *
     * @param text 待检测文本
     * @return true 表示包含 PII
     */
    public boolean containsPii(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        return PII_RULES.values().stream()
                .anyMatch(rule -> rule.pattern.matcher(text).find());
    }

    /**
     * 检测并返回命中的 PII 类型列表.
     *
     * @param text 待检测文本
     * @return 命中的 PII 类型列表，空列表表示无 PII
     */
    public java.util.List<String> detectPiiTypes(String text) {
        if (text == null || text.isEmpty()) {
            return java.util.List.of();
        }

        return PII_RULES.entrySet().stream()
                .filter(entry -> entry.getValue().pattern.matcher(text).find())
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * PII 规则 — Pattern + Replacement 的不可变组合.
     */
    private record PiiRule(Pattern pattern, String replacement) {}
}
