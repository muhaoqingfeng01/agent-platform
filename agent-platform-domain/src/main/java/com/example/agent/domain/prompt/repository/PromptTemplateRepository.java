package com.example.agent.domain.prompt.repository;

import com.example.agent.domain.prompt.entity.PromptTemplate;
import com.example.agent.domain.prompt.valueobject.PromptStatus;

import java.util.List;
import java.util.Optional;

/**
 * 提示词模板仓储接口 — Repository 模式.
 * <p>
 * 实现在 infrastructure 层。封装模板持久化操作，领域层只依赖此接口。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface PromptTemplateRepository {

    /** 保存（新增） */
    void save(PromptTemplate template);

    /** 根据业务 ID 查询 */
    Optional<PromptTemplate> findByPromptId(String promptId);

    /** 分页查询租户下的模板列表 */
    List<PromptTemplate> findByTenantId(Long tenantId, int offset, int size);

    /** 统计租户下的模板数量 */
    long countByTenantId(Long tenantId);

    /** 按状态筛选 */
    List<PromptTemplate> findByTenantIdAndStatus(Long tenantId, PromptStatus status, int offset, int size);

    /** 根据名称查询（同一租户下名称唯一） */
    Optional<PromptTemplate> findByTenantIdAndName(Long tenantId, String name);

    /** 更新模板内容（草稿编辑） */
    void updateContent(String promptId, String name, String description,
                       String templateText, String variablesJson);

    /** 发布：更新版本号 + 状态 + 模板文本 */
    void publish(String promptId, int newVersion, String templateText, String variablesJson);

    /** 更新状态 */
    void updateStatus(String promptId, PromptStatus status);

    /** 软删除 */
    void softDelete(String promptId);

    /** 查询指定名称的最新 PUBLISHED 版本（缓存热点） */
    Optional<PromptTemplate> findLatestPublished(Long tenantId, String name);

    /** 按名称+版本号查询历史版本 */
    Optional<PromptTemplate> findByVersion(Long tenantId, String name, int version);
}
