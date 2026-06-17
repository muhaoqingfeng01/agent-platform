package com.example.agent.application.intent.recognizer;

import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.domain.conversation.entity.Intent;
import com.example.agent.domain.conversation.repository.IntentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 规则匹配器 — Strategy 模式：正则优先 → 关键词加权.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleRecognizer {

    private final IntentRepository intentRepository;

    public Optional<IntentResult> recognize(Long tenantId, String userInput) {
        List<Intent> intents = intentRepository.findActiveByTenant(tenantId);
        if (intents.isEmpty()) {
            return Optional.empty();
        }
        String normalizedInput = normalize(userInput);

        for (Intent intent : intents) {
            List<String> patterns = intent.getPatterns();
            if (patterns == null || patterns.isEmpty()) continue;

            for (String pattern : patterns) {
                if (isRegexPattern(pattern)) {
                    if (Pattern.matches(extractRegex(pattern), normalizedInput)) {
                        return Optional.of(IntentResult.matched(
                                intent.getIntentCode(), intent.getIntentName(), intent.getCategory(),
                                1.0, "正则匹配: " + pattern, intent.getRiskLevel(), intent.getRequiredParams()));
                    }
                }
            }
            double keywordScore = calculateKeywordScore(patterns, normalizedInput);
            if (keywordScore >= 0.7) {
                return Optional.of(IntentResult.matched(
                        intent.getIntentCode(), intent.getIntentName(), intent.getCategory(),
                        keywordScore, "关键词匹配: score=" + String.format("%.2f", keywordScore),
                        intent.getRiskLevel(), intent.getRequiredParams()));
            }
        }
        return Optional.empty();
    }

    private boolean isRegexPattern(String pattern) {
        return pattern.startsWith("/") && pattern.endsWith("/");
    }

    private String extractRegex(String pattern) {
        return pattern.substring(1, pattern.length() - 1);
    }

    private double calculateKeywordScore(List<String> patterns, String input) {
        int totalKeywords = 0, matchedKeywords = 0, matchedChars = 0;
        for (String pattern : patterns) {
            if (!isRegexPattern(pattern)) {
                totalKeywords++;
                if (input.contains(pattern)) {
                    matchedKeywords++;
                    matchedChars += pattern.length();
                }
            }
        }
        if (totalKeywords == 0 || matchedKeywords == 0) return 0.0;
        double baseScore = (double) matchedKeywords / totalKeywords;
        double densityFactor = 1.0 + ((double) matchedChars / input.length()) * 0.2;
        return Math.min(baseScore * densityFactor, 0.9);
    }

    private String normalize(String input) {
        return input.replaceAll("[，。！？、；：\"'（）【】《》\\s]+", "").trim().toLowerCase();
    }
}
