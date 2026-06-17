package com.example.agent.domain.security.repository;

import com.example.agent.domain.security.entity.SensitiveWord;
import com.example.agent.domain.security.valueobject.SensitiveCategory;
import com.example.agent.domain.security.valueobject.SensitiveWordStatus;

import java.util.List;
import java.util.Optional;

/**
 * 敏感词仓储接口 — Domain 层定义，Infrastructure 层实现.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface SensitiveWordRepository {

    /** 保存 */
    void save(SensitiveWord word);

    /** 更新 */
    void update(SensitiveWord word);

    /** 更新状态 */
    void updateStatus(Long id, SensitiveWordStatus status);

    /** 按主键查询 */
    Optional<SensitiveWord> findById(Long id);

    /** 按租户分页查询 */
    List<SensitiveWord> findByTenant(Long tenantId, int page, int size);

    /** 统计租户下的规则数 */
    long countByTenant(Long tenantId);

    /** 查询所有启用的规则（全局 + 指定租户） */
    List<SensitiveWord> findActiveByTenantAndGlobal(Long tenantId);

    /** 按分类查询启用的规则 */
    List<SensitiveWord> findActiveByCategory(Long tenantId, SensitiveCategory category);

    /** 查询全局启用的规则 */
    List<SensitiveWord> findActiveGlobal();

    /** 删除 */
    void deleteById(Long id);
}
