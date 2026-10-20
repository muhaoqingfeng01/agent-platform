package com.example.agent.domain.conversation;

import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;

import java.util.List;
import java.util.Optional;

/**
 * 会话检查点存储端口 — Domain 不依赖 Redis API，Infra 可替换为其它实现.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
public interface ConversationCheckpointStore {

    /**
     * 追加一条检查点并裁剪为最近 N 条（N 由配置决定，默认 5）.
     */
    void save(Long tenantId, String conversationId, ConversationCheckpoint checkpoint);

    /**
     * 读取最新一条；反序列化失败时应跳过坏数据并尝试更早的条目，全部失败则 empty.
     */
    Optional<ConversationCheckpoint> loadLatest(Long tenantId, String conversationId);

    /**
     * 按时间正序返回当前保留的全部检查点（最旧在前）.
     */
    List<ConversationCheckpoint> loadAll(Long tenantId, String conversationId);

    void clear(Long tenantId, String conversationId);
}
