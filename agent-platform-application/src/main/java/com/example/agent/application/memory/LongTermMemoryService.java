package com.example.agent.application.memory;

import com.example.agent.application.memory.extractor.MemoryExtractor;
import com.example.agent.application.memory.extractor.MemoryExtractorRegistry;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.repository.LongTermMemoryRepository;
import com.example.agent.domain.conversation.valueobject.MemoryType;
import com.example.agent.infrastructure.config.ai.DeepSeekUtilityClient;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 长期记忆应用服务 — Template Method + Factory 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LongTermMemoryService {

    private final LongTermMemoryRepository memoryRepository;
    private final SessionMemoryService sessionMemoryService;
    private final DeepSeekUtilityClient deepSeekUtilityClient;
    private final MemoryExtractorRegistry extractorRegistry;
    /** 🆕 P6 配置治理子方案03: 长期记忆参数从 Nacos 动态读取 */
    private final SessionConfig sessionConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public void extractAndSave(String conversationId, String userId, Long tenantId) {
        log.info("[LTM] 开始提取: convId={}", conversationId);
        try {
            List<Message> history = sessionMemoryService.getRecentMessages(conversationId, sessionConfig.getLongTermMemoryMaxRounds());
            if (history.size() < 4) {
                log.debug("[LTM] 对话轮次不足，跳过: convId={}", conversationId);
                return;
            }
            String summary = summarizeConversation(history);
            List<MemoryFact> facts = parseFacts(summary);

            for (MemoryFact fact : facts) {
                MemoryExtractor extractor = extractorRegistry.get(fact.getType());
                List<LongTermMemory> memories = extractor.extract(fact, history);
                for (LongTermMemory memory : memories) {
                    memory.setTenantId(tenantId);
                    memory.setUserId(userId);
                    memory.setSource(conversationId);
                    memoryRepository.upsert(memory);
                }
            }
            log.info("[LTM] 提取完成: convId={}, facts={}", conversationId, facts.size());
        } catch (RestClientException e) {
            // fail-open：LLM 瞬时失败不影响主对话
            log.warn("[LTM] 提取跳过（LLM 调用失败）: convId={}, error={}", conversationId, e.getMessage());
        } catch (Exception e) {
            log.error("[LTM] 提取失败: convId={}", conversationId, e);
        }
    }

    public List<LongTermMemory> loadUserMemories(String userId) {
        Long tenantId = TenantContext.getCurrentTenantId();
        List<LongTermMemory> memories = memoryRepository.findByUserId(tenantId, userId);
        return memories.stream()
                .filter(m -> m.getExpireAt() == null || m.getExpireAt().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparingInt(m -> m.getMemoryType().getLoadPriority()))
                .toList();
    }

    public String injectMemoriesIntoPrompt(String systemPrompt, List<LongTermMemory> memories) {
        if (memories.isEmpty()) return systemPrompt;
        StringBuilder block = new StringBuilder("\n\n## 关于该用户的已知信息\n");
        for (LongTermMemory m : memories) {
            block.append("- ").append(m.getMemoryKey()).append(": ").append(m.getMemoryValue())
                    .append(" (置信度: ").append(String.format("%.0f%%", m.getConfidence() * 100)).append(")\n");
        }
        return systemPrompt + block;
    }

    private String summarizeConversation(List<Message> history) {
        String conversationText = history.stream()
                .map(m -> m.getRole().getDesc() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        // 走工具型客户端：强制 thinking=disabled，避免 Spring AI 非流式 JsonEOF
        return deepSeekUtilityClient.completeText(String.format("""
                从对话中提取用户关键信息。只返回 JSON，不要其它文字:
                {"facts":[{"type":"FACT","key":"...","value":"...","confidence":0.9}]}
                type: FACT/PREFERENCE/CONTEXT/SUMMARY

                对话内容:
                %s
                """, conversationText), 1024);
    }

    private List<MemoryFact> parseFacts(String llmResponse) {
        try {
            String json = extractJson(llmResponse);
            JsonNode root = objectMapper.readTree(json);
            List<MemoryFact> facts = new ArrayList<>();
            for (JsonNode node : root.path("facts")) {
                facts.add(MemoryFact.builder()
                        .type(MemoryType.fromCode(node.path("type").asText("FACT")))
                        .key(node.path("key").asText())
                        .value(node.path("value").asText())
                        .confidence(node.path("confidence").asDouble(0.8))
                        .build());
            }
            return facts;
        } catch (Exception e) {
            log.error("[LTM] 解析 LLM 输出失败", e);
            return List.of();
        }
    }

    private String extractJson(String text) {
        if (text.contains("```json")) {
            int s = text.indexOf("```json") + 7;
            int e = text.indexOf("```", s);
            return e > s ? text.substring(s, e).trim() : text;
        }
        return text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryFact {
        private MemoryType type;
        private String key;
        private String value;
        private double confidence;
    }
}
