package com.example.agent.application.memory.extractor;

import com.example.agent.application.memory.LongTermMemoryService.MemoryFact;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.MemoryType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 事实提取器 — 永不过期.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Component
public class FactMemoryExtractor implements MemoryExtractor {

    @Override
    public MemoryType supportedType() { return MemoryType.FACT; }

    @Override
    public List<LongTermMemory> extract(MemoryFact fact, List<Message> history) {
        return List.of(LongTermMemory.builder()
                .memoryType(MemoryType.FACT)
                .memoryKey(fact.getKey())
                .memoryValue(fact.getValue())
                .confidence(fact.getConfidence())
                .expireAt(null)
                .build());
    }
}
