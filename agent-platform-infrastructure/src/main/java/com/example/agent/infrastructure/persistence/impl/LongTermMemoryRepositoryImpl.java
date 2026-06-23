package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.repository.LongTermMemoryRepository;
import com.example.agent.domain.conversation.valueobject.MemoryType;
import com.example.agent.infrastructure.persistence.mapper.LongTermMemoryMapper;
import com.example.agent.infrastructure.persistence.po.LongTermMemoryPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 长期记忆仓储 MyBatis 实现 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class LongTermMemoryRepositoryImpl implements LongTermMemoryRepository {

    private final LongTermMemoryMapper memoryMapper;

    @Override
    public void upsert(LongTermMemory memory) {
        memoryMapper.upsert(toPO(memory));
    }

    @Override
    public void save(LongTermMemory memory) {
        memoryMapper.insert(toPO(memory));
    }

    @Override
    public void deleteById(Long id) {
        memoryMapper.deleteById(id);
    }

    @Override
    public int deleteExpired() {
        return memoryMapper.deleteExpired();
    }

    @Override
    public void deleteBySource(String source) {
        memoryMapper.deleteBySource(source);
    }

    @Override
    public List<LongTermMemory> findByUserId(Long tenantId, String userId) {
        return memoryMapper.selectByUserId(tenantId, userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<LongTermMemory> findByUserIdAndType(Long tenantId, String userId, MemoryType type) {
        return memoryMapper.selectByUserIdAndType(tenantId, userId, type.getCode())
                .stream().map(this::toDomain).toList();
    }

    // ==================== 映射方法 ====================

    private LongTermMemory toDomain(LongTermMemoryPO po) {
        return LongTermMemory.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .userId(po.getUserId())
                .memoryType(MemoryType.fromCode(po.getMemoryType()))
                .memoryKey(po.getMemoryKey())
                .memoryValue(po.getMemoryValue())
                .confidence(po.getConfidence())
                .source(po.getSource())
                .expireAt(po.getExpireAt())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private LongTermMemoryPO toPO(LongTermMemory memory) {
        return LongTermMemoryPO.builder()
                .id(memory.getId())
                .tenantId(memory.getTenantId())
                .userId(memory.getUserId())
                .memoryType(memory.getMemoryType().getCode())
                .memoryKey(memory.getMemoryKey())
                .memoryValue(memory.getMemoryValue())
                .confidence(memory.getConfidence())
                .source(memory.getSource())
                .expireAt(memory.getExpireAt())
                .createdAt(memory.getCreatedAt())
                .updatedAt(memory.getUpdatedAt())
                .build();
    }
}
