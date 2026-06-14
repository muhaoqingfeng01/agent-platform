package com.example.agent.application.memory.extractor;

import com.example.agent.application.memory.LongTermMemoryService.MemoryFact;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.MemoryType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 上下文提取器 — 7 天过期.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Component
public class ContextMemoryExtractor implements MemoryExtractor {

    @Override
    public MemoryType supportedType() { return MemoryType.CONTEXT; }

    @Override
    public List<LongTermMemory> extract(MemoryFact fact, List<Message> history) {
        return List.of(LongTermMemory.builder()
                .memoryType(MemoryType.CONTEXT)
                .memoryKey(fact.getKey())
                .memoryValue(fact.getValue())
                .confidence(fact.getConfidence())
                .expireAt(LocalDateTime.now().plusDays(7))
                .build());
    }
}
