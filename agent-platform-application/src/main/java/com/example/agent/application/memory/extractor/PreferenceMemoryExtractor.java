package com.example.agent.application.memory.extractor;

import com.example.agent.application.memory.LongTermMemoryService.MemoryFact;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.MemoryType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 偏好提取器 — 90 天过期.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Component
public class PreferenceMemoryExtractor implements MemoryExtractor {

    @Override
    public MemoryType supportedType() { return MemoryType.PREFERENCE; }

    @Override
    public List<LongTermMemory> extract(MemoryFact fact, List<Message> history) {
        return List.of(LongTermMemory.builder()
                .memoryType(MemoryType.PREFERENCE)
                .memoryKey(fact.getKey())
                .memoryValue(fact.getValue())
                .confidence(fact.getConfidence())
                .expireAt(LocalDateTime.now().plusDays(90))
                .build());
    }
}
