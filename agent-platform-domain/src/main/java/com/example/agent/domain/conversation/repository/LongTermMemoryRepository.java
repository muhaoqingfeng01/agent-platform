package com.example.agent.domain.conversation.repository;

import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.valueobject.MemoryType;

import java.util.List;

/**
 * 长期记忆仓储接口 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface LongTermMemoryRepository {

    void upsert(LongTermMemory memory);

    void save(LongTermMemory memory);

    void deleteById(Long id);

    int deleteExpired();

    void deleteBySource(String source);

    List<LongTermMemory> findByUserId(String tenantId, String userId);

    List<LongTermMemory> findByUserIdAndType(String tenantId, String userId, MemoryType type);
}
