package com.example.agent.domain.conversation.entity;

import com.example.agent.domain.conversation.valueobject.MemoryType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 长期记忆实体 — Builder 模式构建.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder(toBuilder = true)
public class LongTermMemory {

    private Long id;
    private Long tenantId;
    private String userId;
    private MemoryType memoryType;
    private String memoryKey;
    private String memoryValue;
    private Double confidence;
    private String source;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 置信度是否达到可靠阈值 */
    public boolean isReliable() {
        return confidence != null && confidence >= 0.7;
    }

    /** 是否已过期 */
    public boolean isExpired() {
        return expireAt != null && expireAt.isBefore(LocalDateTime.now());
    }
}
