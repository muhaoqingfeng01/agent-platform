package com.example.agent.application.memory.extractor;

import com.example.agent.application.memory.LongTermMemoryService.MemoryFact;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.MemoryType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 摘要提取器 — 30 天过期.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Component
public class SummaryMemoryExtractor implements MemoryExtractor {

    @Override
    public MemoryType supportedType() { return MemoryType.SUMMARY; }

    @Override
    public List<LongTermMemory> extract(MemoryFact fact, List<Message> history) {
        return List.of(LongTermMemory.builder()
                .memoryType(MemoryType.SUMMARY)
                .memoryKey("conversation_summary")
                .memoryValue(fact.getValue())
                .confidence(fact.getConfidence())
                .expireAt(LocalDateTime.now().plusDays(30))
                .build());
    }
}
