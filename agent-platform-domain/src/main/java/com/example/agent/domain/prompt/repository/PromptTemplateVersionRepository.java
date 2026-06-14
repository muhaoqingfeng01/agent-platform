package com.example.agent.domain.prompt.repository;

import com.example.agent.domain.prompt.entity.PromptTemplateVersion;

import java.util.List;
import java.util.Optional;

/**
 * 提示词版本历史仓储接口 — Memento 模式.
 * <p>
 * 每次发布/回滚生成一条版本快照记录，支持历史追溯和版本对比。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface PromptTemplateVersionRepository {

    /** 保存版本快照 */
    void save(PromptTemplateVersion version);

    /** 查询指定版本 */
    Optional<PromptTemplateVersion> findByPromptIdAndVersion(String promptId, int version);

    /** 查询某模板的所有历史版本（按版本号降序） */
    List<PromptTemplateVersion> findByPromptId(String promptId);

    /** 获取某模板的版本数量 */
    int countByPromptId(String promptId);

    /** 获取某模板的最新版本号 */
    Optional<Integer> findMaxVersionByPromptId(String promptId);
}
