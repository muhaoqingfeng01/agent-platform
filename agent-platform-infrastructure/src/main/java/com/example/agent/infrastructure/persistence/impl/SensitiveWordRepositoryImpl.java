package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.security.entity.SensitiveWord;
import com.example.agent.domain.security.repository.SensitiveWordRepository;
import com.example.agent.domain.security.valueobject.ActionType;
import com.example.agent.domain.security.valueobject.MatchType;
import com.example.agent.domain.security.valueobject.SensitiveCategory;
import com.example.agent.domain.security.valueobject.SensitiveWordStatus;
import com.example.agent.domain.security.valueobject.SeverityLevel;
import com.example.agent.infrastructure.persistence.mapper.SensitiveWordMapper;
import com.example.agent.infrastructure.persistence.po.SensitiveWordPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 敏感词仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class SensitiveWordRepositoryImpl implements SensitiveWordRepository {

    private final SensitiveWordMapper mapper;

    @Override
    public void save(SensitiveWord word) {
        mapper.insert(toPO(word));
    }

    @Override
    public void update(SensitiveWord word) {
        mapper.update(toPO(word));
    }

    @Override
    public void updateStatus(Long id, SensitiveWordStatus status) {
        mapper.updateStatus(id, status.name());
    }

    @Override
    public Optional<SensitiveWord> findById(Long id) {
        SensitiveWordPO po = mapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<SensitiveWord> findByTenant(String tenantId, int page, int size) {
        return mapper.selectByTenant(tenantId, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public long countByTenant(String tenantId) {
        return mapper.countByTenant(tenantId);
    }

    @Override
    public List<SensitiveWord> findActiveByTenantAndGlobal(String tenantId) {
        return mapper.selectActiveByTenantAndGlobal(tenantId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<SensitiveWord> findActiveByCategory(String tenantId, SensitiveCategory category) {
        return mapper.selectActiveByCategory(tenantId, category.name()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<SensitiveWord> findActiveGlobal() {
        return mapper.selectActiveGlobal().stream()
                .map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    // ==================== 映射方法 ====================

    private SensitiveWord toDomain(SensitiveWordPO po) {
        return SensitiveWord.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .word(po.getWord())
                .matchType(MatchType.fromCode(po.getMatchType()))
                .category(SensitiveCategory.fromCode(po.getCategory()))
                .severity(SeverityLevel.fromCode(po.getSeverity()))
                .action(ActionType.fromCode(po.getAction()))
                .status(SensitiveWordStatus.fromCode(po.getStatus()))
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private SensitiveWordPO toPO(SensitiveWord word) {
        return SensitiveWordPO.builder()
                .id(word.getId())
                .tenantId(word.getTenantId())
                .word(word.getWord())
                .matchType(word.getMatchType().name())
                .category(word.getCategory().name())
                .severity(word.getSeverity().name())
                .action(word.getAction().name())
                .status(word.getStatus().name())
                .createdAt(word.getCreatedAt())
                .updatedAt(word.getUpdatedAt())
                .build();
    }
}
