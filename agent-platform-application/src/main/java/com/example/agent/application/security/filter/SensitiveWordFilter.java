package com.example.agent.application.security.filter;

import cn.hutool.dfa.WordTree;
import com.example.agent.domain.security.entity.SensitiveWord;
import com.example.agent.domain.security.repository.SensitiveWordRepository;
import com.example.agent.domain.security.valueobject.ActionType;
import com.example.agent.domain.security.valueobject.MatchType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

/**
 * 敏感词过滤器 — order=3.
 *
 * <p>从数据库加载 ACTIVE 敏感词，使用 Aho-Corasick 多模匹配（Hutool WordTree）
 * 实现 O(n) 时间复杂度的高效匹配。
 * <p>每 5 分钟自动刷新缓存中的敏感词列表。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SensitiveWordFilter implements InputFilter {

    private final SensitiveWordRepository sensitiveWordRepository;

    /** EXACT 匹配的敏感词（Hutool WordTree 实现 Aho-Corasick，线程安全） */
    private volatile WordTree exactWordTree = new WordTree();

    /** REGEX 模式的敏感词 Map（word -> SensitiveWord 元数据） */
    private final Map<String, SensitiveWord> regexWords = new ConcurrentHashMap<>();

    /** 读写锁 — 保证缓存刷新时的线程安全 */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public int order() {
        return 3;
    }

    /**
     * 启动时加载 + 每 5 分钟刷新缓存.
     */
    @PostConstruct
    @Scheduled(fixedDelay = 300_000)
    public void refreshCache() {
        lock.writeLock().lock();
        try {
            // 从 DB 加载全局 + 当前所有租户的启用规则
            // 注：此处加载全局规则；租户级规则在 filter() 中按需使用
            List<SensitiveWord> words = sensitiveWordRepository.findActiveGlobal();

            WordTree newTree = new WordTree();
            Map<String, SensitiveWord> newRegex = new ConcurrentHashMap<>();

            for (SensitiveWord sw : words) {
                if (sw.getMatchType() == MatchType.EXACT) {
                    newTree.addWord(sw.getWord());
                } else if (sw.getMatchType() == MatchType.REGEX) {
                    newRegex.put(sw.getWord(), sw);
                }
            }

            this.exactWordTree = newTree;
            this.regexWords.clear();
            this.regexWords.putAll(newRegex);

            log.info("[SensitiveWordFilter] 敏感词缓存刷新完成: exact={}, regex={}",
                    newTree.size(), newRegex.size());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public FilterResult filter(String content, FilterContext context) {
        if (content == null || content.isEmpty()) {
            return FilterResult.pass();
        }

        // 加载租户专属规则（与全局规则合并检测）
        List<SensitiveWord> tenantWords = sensitiveWordRepository
                .findActiveByTenantAndGlobal(context.getTenantId());

        lock.readLock().lock();
        try {
            // 1. EXACT 匹配 — 使用 Aho-Corasick
            List<String> matched = exactWordTree.matchAll(content, -1, false, false);
            if (!matched.isEmpty()) {
                // 在租户规则中查找匹配词，获取其 action
                for (String match : matched) {
                    SensitiveWord rule = findRuleByWord(match, tenantWords);
                    if (rule != null) {
                        return handleMatch(rule, match, context);
                    }
                }
                // 未在规则中找到（不应出现），默认阻断
                String matchedWord = matched.get(0);
                log.warn("[SensitiveWordFilter] 全局敏感词命中: word={}, conversationId={}",
                        matchedWord, context.getConversationId());
                return FilterResult.block("SENSITIVE_WORD",
                        "输入内容包含违规词汇，已被安全系统拦截",
                        matchedWord);
            }

            // 2. REGEX 匹配
            for (SensitiveWord sw : tenantWords) {
                if (sw.getMatchType() == MatchType.REGEX) {
                    try {
                        if (Pattern.compile(sw.getWord()).matcher(content).find()) {
                            return handleMatch(sw, sw.getWord(), context);
                        }
                    } catch (Exception e) {
                        log.warn("[SensitiveWordFilter] 正则匹配异常: word={}", sw.getWord(), e);
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        // 3. 也检查内存中的 REGEX 缓存
        for (Map.Entry<String, SensitiveWord> entry : regexWords.entrySet()) {
            try {
                if (Pattern.compile(entry.getKey()).matcher(content).find()) {
                    return handleMatch(entry.getValue(), entry.getKey(), context);
                }
            } catch (Exception e) {
                log.warn("[SensitiveWordFilter] 缓存正则匹配异常: word={}", entry.getKey(), e);
            }
        }

        return FilterResult.pass();
    }

    /**
     * 根据命中的词和规则决定处理方式.
     */
    private FilterResult handleMatch(SensitiveWord rule, String matchedWord, FilterContext context) {
        log.warn("[SensitiveWordFilter] 敏感词命中: word={}, severity={}, action={}, conversationId={}",
                matchedWord, rule.getSeverity(), rule.getAction(), context.getConversationId());

        if (rule.getAction() == ActionType.BLOCK) {
            return FilterResult.block("SENSITIVE_WORD",
                    "输入内容包含违规词汇，已被安全系统拦截",
                    matchedWord);
        }
        // LOG / WARN — 不阻断，仅记录
        FilterResult result = FilterResult.pass();
        result.setMatchedPattern(matchedWord);
        result.setEventType("SENSITIVE_WORD");
        result.setSeverity(rule.getSeverity().name());
        return result;
    }

    /**
     * 在规则列表中按 word 查找.
     */
    private SensitiveWord findRuleByWord(String match, List<SensitiveWord> rules) {
        return rules.stream()
                .filter(r -> r.getMatchType() == MatchType.EXACT && r.getWord().equals(match))
                .findFirst()
                .orElse(null);
    }
}
