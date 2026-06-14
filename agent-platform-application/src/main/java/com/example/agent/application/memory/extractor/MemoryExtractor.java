package com.example.agent.application.memory.extractor;

import com.example.agent.application.memory.LongTermMemoryService;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.MemoryType;

import java.util.List;

/**
 * 记忆提取器接口 — Strategy 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface MemoryExtractor {

    MemoryType supportedType();

    List<LongTermMemory> extract(LongTermMemoryService.MemoryFact fact, List<Message> history);
}
