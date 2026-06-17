package com.example.agent.application.intent;

import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.application.intent.recognizer.CacheRecognizer;
import com.example.agent.application.intent.recognizer.LLMRecognizer;
import com.example.agent.application.intent.recognizer.RuleRecognizer;
import com.example.agent.infrastructure.annotation.Auditable;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 意图识别责任链 — Chain of Responsibility 模式.
 * <p>
 * 三层识别：规则（~1ms）→ 缓存（~5ms）→ LLM（~500ms），命中即短路.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntentRecognitionChain {

    private final RuleRecognizer ruleRecognizer;
    private final CacheRecognizer cacheRecognizer;
    private final LLMRecognizer llmRecognizer;

    public IntentResult recognize(String userInput) {
        return recognize(TenantContext.getCurrentTenantId(), userInput);
    }

    /**
     * 意图识别（显式传入 tenantId）.
     * <p>用于异步/线程池场景（如 SSE 流式），此时 ThreadLocal 中的租户上下文不可用.
     */
    @Auditable(action = "INTENT_RECOGNITION", resourceType = "INTENT", recordResponse = false)
    public IntentResult recognize(String tenantId, String userInput) {
        long startTime = System.currentTimeMillis();

        try {
            // Layer 1: 规则匹配
            Optional<IntentResult> ruleResult = ruleRecognizer.recognize(tenantId, userInput);
            if (ruleResult.isPresent() && ruleResult.get().getConfidence() >= 0.9) {
                log.debug("[Intent] Layer1 命中: intent={}, cost={}ms",
                        ruleResult.get().getIntentCode(), System.currentTimeMillis() - startTime);
                return ruleResult.get();
            }

            // Layer 2: Redis 缓存
            Optional<IntentResult> cachedResult = cacheRecognizer.recognize(tenantId, userInput);
            if (cachedResult.isPresent()) {
                log.debug("[Intent] Layer2 命中: intent={}, cost={}ms",
                        cachedResult.get().getIntentCode(), System.currentTimeMillis() - startTime);
                return cachedResult.get();
            }

            // Layer 3: LLM 兜底
            IntentResult llmResult = llmRecognizer.recognize(tenantId, userInput);
            cacheRecognizer.cacheResult(tenantId, userInput, llmResult);

            log.info("[Intent] Layer3 兜底: intent={}, confidence={}, cost={}ms",
                    llmResult.getIntentCode(), llmResult.getConfidence(),
                    System.currentTimeMillis() - startTime);
            return llmResult;

        } catch (Exception e) {
            log.error("[Intent] 识别异常: input={}", userInput, e);
            return IntentResult.unknown(e.getMessage());
        }
    }
}
