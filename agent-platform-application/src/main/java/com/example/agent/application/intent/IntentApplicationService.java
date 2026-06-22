package com.example.agent.application.intent;

import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.application.intent.recognizer.CacheRecognizer;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.conversation.entity.Intent;
import com.example.agent.domain.conversation.repository.IntentRepository;
import com.example.agent.domain.conversation.valueobject.IntentStatus;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 意图应用服务 — Template Method 统一 CRUD 骨架.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntentApplicationService {

    private final IntentRepository intentRepository;
    private final CacheRecognizer cacheRecognizer;
    private final IntentRecognitionChain recognitionChain;

    @Transactional
    public IntentResponse createIntent(CreateIntentRequest request) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (intentRepository.existsByTenantAndCode(tenantId, request.getIntentCode())) {
            throw new BusinessException(400, "意图编码已存在: " + request.getIntentCode());
        }
        Intent intent = Intent.builder()
                .intentId(IdGenerator.generate("int"))
                .tenantId(tenantId)
                .intentCode(request.getIntentCode())
                .intentName(request.getIntentName())
                .category(request.getCategory() != null ? request.getCategory() : "FAQ")
                .patterns(request.getPatterns())
                .examples(request.getExamples())
                .llmPrompt(request.getLlmPrompt())
                .requiredParams(request.getRequiredParams())
                .riskLevel(request.getRiskLevel() != null ? request.getRiskLevel() : "LOW")
                .status(IntentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        intentRepository.save(intent);
        log.info("[Intent] 创建: id={}, code={}", intent.getIntentId(), intent.getIntentCode());
        return IntentResponse.from(intent);
    }

    @Transactional
    public IntentResponse updateIntent(String intentId, UpdateIntentRequest request) {
        Intent intent = intentRepository.findByIntentId(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("意图", intentId));
        intent.updateDefinition(request.getIntentName(), request.getPatterns(), request.getExamples(),
                request.getLlmPrompt(), request.getRequiredParams(), request.getRiskLevel());
        intentRepository.update(intent);
        cacheRecognizer.evictCache(intent.getTenantId());
        log.info("[Intent] 更新: id={}", intentId);
        return IntentResponse.from(intent);
    }

    @Transactional
    public void toggleStatus(String intentId, IntentStatus newStatus) {
        Intent intent = intentRepository.findByIntentId(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("意图", intentId));
        intent.setStatus(newStatus);
        intentRepository.updateStatus(intentId, newStatus);
        cacheRecognizer.evictCache(intent.getTenantId());
        log.info("[Intent] 状态变更: id={}, status={}", intentId, newStatus);
    }

    public PageResponse<IntentResponse> listIntents(int page, int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        List<Intent> intents = intentRepository.findByTenant(tenantId, page, size);
        long total = intentRepository.countByTenant(tenantId);
        return PageResponse.of(intents.stream().map(IntentResponse::from).toList(), total, page, size);
    }

    public IntentResponse getIntent(String intentId) {
        Intent intent = intentRepository.findByIntentId(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("意图", intentId));
        return IntentResponse.from(intent);
    }

    @Transactional
    public void deleteIntent(String intentId) {
        Intent intent = intentRepository.findByIntentId(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("意图", intentId));
        intentRepository.softDelete(intentId);
        cacheRecognizer.evictCache(intent.getTenantId());
        log.info("[Intent] 删除: id={}", intentId);
    }

    public IntentTestResponse testRecognition(String intentId, String testInput) {
        Intent intent = intentRepository.findByIntentId(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("意图", intentId));
        long startTime = System.nanoTime();
        IntentResult result = recognitionChain.recognize(testInput);
        double costMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return IntentTestResponse.builder()
                .input(testInput)
                .expectedIntentCode(intent.getIntentCode())
                .actualIntentCode(result.getIntentCode())
                .matched(result.getIntentCode().equals(intent.getIntentCode()))
                .confidence(result.getConfidence())
                .matchedLayer(result.getMatchedLayer())
                .reasoning(result.getReasoning())
                .costMs(costMs)
                .build();
    }

    public BatchTestResponse batchTest(List<TestItem> items) {
        int total = items.size(), correct = 0;
        List<IntentTestResponse> details = new ArrayList<>();
        for (TestItem item : items) {
            IntentResult result = recognitionChain.recognize(item.getInput());
            boolean matched = result.getIntentCode().equals(item.getExpectedIntentCode());
            if (matched) correct++;
            details.add(IntentTestResponse.builder()
                    .input(item.getInput()).expectedIntentCode(item.getExpectedIntentCode())
                    .actualIntentCode(result.getIntentCode()).matched(matched)
                    .confidence(result.getConfidence()).build());
        }
        return BatchTestResponse.builder().total(total).correct(correct)
                .accuracy(total > 0 ? (double) correct / total : 0.0).details(details).build();
    }

    // ==================== DTOs ====================

    @Data
    public static class CreateIntentRequest {
        private String intentCode;
        private String intentName;
        private String category;
        private List<String> patterns;
        private List<String> examples;
        private String llmPrompt;
        private List<java.util.Map<String, Object>> requiredParams;
        private String riskLevel;
    }

    @Data
    public static class UpdateIntentRequest {
        private String intentName;
        private List<String> patterns;
        private List<String> examples;
        private String llmPrompt;
        private List<java.util.Map<String, Object>> requiredParams;
        private String riskLevel;
    }

    @Data
    @Builder
    public static class IntentResponse {
        private String intentId;
        private String intentCode;
        private String intentName;
        private String category;
        private List<String> patterns;
        private List<String> examples;
        private String llmPrompt;
        private List<java.util.Map<String, Object>> requiredParams;
        private String riskLevel;
        private String status;
        private Long createdAt;

        public static IntentResponse from(Intent intent) {
            return IntentResponse.builder()
                    .intentId(intent.getIntentId())
                    .intentCode(intent.getIntentCode())
                    .intentName(intent.getIntentName())
                    .category(intent.getCategory())
                    .patterns(intent.getPatterns())
                    .examples(intent.getExamples())
                    .llmPrompt(intent.getLlmPrompt())
                    .requiredParams(intent.getRequiredParams())
                    .riskLevel(intent.getRiskLevel())
                    .status(intent.getStatus().name())
                    .createdAt(TimeConverters.toEpochMilli(intent.getCreatedAt()))
                    .build();
        }
    }

    @Data @Builder
    public static class IntentTestResponse {
        private String input;
        private String expectedIntentCode;
        private String actualIntentCode;
        private boolean matched;
        private double confidence;
        private String matchedLayer;
        private String reasoning;
        private double costMs;
    }

    @Data @Builder
    public static class BatchTestResponse {
        private int total, correct;
        private double accuracy;
        private List<IntentTestResponse> details;
    }

    @Data
    public static class TestItem {
        private String input;
        private String expectedIntentCode;
    }
}
