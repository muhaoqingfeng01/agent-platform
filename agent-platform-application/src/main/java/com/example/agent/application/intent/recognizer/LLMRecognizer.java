package com.example.agent.application.intent.recognizer;

import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.domain.conversation.entity.Intent;
import com.example.agent.domain.conversation.repository.IntentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * LLM 意图分类器 — Template Method 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LLMRecognizer {

    private final IntentRepository intentRepository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IntentResult recognize(Long tenantId, String userInput) {
        List<Intent> intents = intentRepository.findActiveByTenant(tenantId);
        if (intents.isEmpty()) {
            return IntentResult.unknown("当前租户未配置任何活跃意图");
        }
        String prompt = buildClassificationPrompt(intents, userInput);
        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            return parseIntentFromLLM(response, intents);
        } catch (Exception e) {
            log.error("[Intent] LLM 分类失败", e);
            return IntentResult.unknown("LLM 分类失败: " + e.getMessage());
        }
    }

    protected String buildClassificationPrompt(List<Intent> intents, String userInput) {
        StringBuilder intentsBlock = new StringBuilder();
        for (Intent intent : intents) {
            String examples = intent.getExamples() != null ? String.join(", ", intent.getExamples()) : "无";
            intentsBlock.append(String.format(
                    "- **编码**: %s  **名称**: %s  **分类**: %s  **示例问法**: %s\n",
                    intent.getIntentCode(), intent.getIntentName(), intent.getCategory(), examples));
        }

        return String.format("""
            你是一个意图分类器，将用户输入归类到预定义的意图中。

            ## 意图列表
            %s

            ## 用户输入
            > %s

            ## 输出格式（严格遵守 JSON）
            {"intent_code":"意图编码","confidence":0.95,"reasoning":"匹配理由"}
            如果不匹配任何意图，intent_code 为 "UNKNOWN"
            """, intentsBlock, userInput);
    }

    private IntentResult parseIntentFromLLM(String llmResponse, List<Intent> intents) {
        try {
            String json = extractJsonBlock(llmResponse);
            JsonNode node = objectMapper.readTree(json);
            String intentCode = node.path("intent_code").asText("UNKNOWN");
            double confidence = node.path("confidence").asDouble(0.5);
            String reasoning = node.path("reasoning").asText("");

            Intent matchedIntent = intents.stream()
                    .filter(i -> i.getIntentCode().equals(intentCode))
                    .findFirst().orElse(null);

            if (matchedIntent == null || "UNKNOWN".equals(intentCode)) {
                return IntentResult.unknown(reasoning);
            }
            return IntentResult.matched(
                    matchedIntent.getIntentCode(), matchedIntent.getIntentName(),
                    matchedIntent.getCategory(), confidence, reasoning,
                    matchedIntent.getRiskLevel(), matchedIntent.getRequiredParams(), "LLM");
        } catch (Exception e) {
            log.error("[Intent] LLM 输出解析失败: response={}", llmResponse, e);
            return IntentResult.unknown("LLM 输出解析失败");
        }
    }

    private String extractJsonBlock(String text) {
        if (text.contains("```json")) {
            int start = text.indexOf("```json") + 7;
            int end = text.indexOf("```", start);
            if (end > start) return text.substring(start, end).trim();
        }
        if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.indexOf("```", start);
            if (end > start) return text.substring(start, end).trim();
        }
        return text.trim();
    }
}
