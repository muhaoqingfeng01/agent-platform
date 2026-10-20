package com.example.agent.application.checkpoint;

import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.conversation.ConversationCheckpointStore;
import com.example.agent.domain.conversation.valueobject.AgentPhase;
import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会话检查点应用服务 — 状态迁移后写入、新 stream 恢复、会话关闭清理.
 * <p>
 * B7 合法相位迁移只需调用 {@link #recordTransition}；不要在每个 token 上写 Redis。
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckpointService {

    public static final String ATTR_RESTORED_CHECKPOINT = "restoredCheckpoint";
    public static final String ATTR_SLOT_COLLECTION = "slotCollection";

    private final ConversationCheckpointStore checkpointStore;
    private final SlotCollectionCodec slotCodec;

    /**
     * 合法相位迁移后保存检查点（会话级快照）.
     */
    public ConversationCheckpoint recordTransition(String conversationId, AgentPhase phase,
                                                    String intentCode, SlotCollection slots,
                                                    String executionId, Integer stepIndex) {
        if (conversationId == null || conversationId.isBlank() || phase == null) {
            return null;
        }
        ConversationCheckpoint checkpoint = ConversationCheckpoint.builder()
                .checkpointId(IdGenerator.generate("ckpt"))
                .conversationId(conversationId)
                .state(phase)
                .intentCode(intentCode)
                .slotsJson(slotCodec.encode(slots))
                .executionId(executionId)
                .stepIndex(stepIndex)
                .createdAt(LocalDateTime.now())
                .build();
        checkpointStore.save(currentTenantId(), conversationId, checkpoint);
        return checkpoint;
    }

    public Optional<ConversationCheckpoint> loadLatest(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            return Optional.empty();
        }
        return checkpointStore.loadLatest(currentTenantId(), conversationId);
    }

    public List<ConversationCheckpoint> loadAll(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            return List.of();
        }
        return checkpointStore.loadAll(currentTenantId(), conversationId);
    }

    public void clear(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            return;
        }
        checkpointStore.clear(currentTenantId(), conversationId);
    }

    /**
     * 新 stream 入口：仅当最新检查点可续传（state ≠ INIT/DONE/ERROR）时返回.
     * 反序列化失败由 Store 吞掉坏条目；全部失败则 empty，调用方从 INIT 开始。
     */
    public Optional<ConversationCheckpoint> restoreForNewStream(String conversationId) {
        Optional<ConversationCheckpoint> latest = loadLatest(conversationId);
        if (latest.isEmpty()) {
            return Optional.empty();
        }
        ConversationCheckpoint checkpoint = latest.get();
        if (!checkpoint.isResumable()) {
            log.debug("[Checkpoint] 最新相位不可续传，按 INIT: convId={}, state={}",
                    conversationId, checkpoint.stateCode());
            return Optional.empty();
        }
        log.info("[Checkpoint] 断点恢复: convId={}, state={}, intent={}, step={}",
                conversationId, checkpoint.stateCode(), checkpoint.getIntentCode(), checkpoint.getStepIndex());
        return Optional.of(checkpoint);
    }

    /**
     * 将可续传检查点注入交互上下文，供各策略复用，避免重复读 Redis.
     */
    public void attachLatest(InteractionContext context) {
        if (context == null || context.getConversationId() == null || context.getConversationId().isBlank()) {
            return;
        }
        restoreForNewStream(context.getConversationId()).ifPresent(checkpoint -> {
            context.putAttribute(ATTR_RESTORED_CHECKPOINT, checkpoint);
            context.putAttribute(ATTR_SLOT_COLLECTION, slotCodec.decode(checkpoint.getSlotsJson()));
        });
    }

    public Optional<ConversationCheckpoint> restoredOf(InteractionContext context) {
        if (context == null) {
            return Optional.empty();
        }
        Object raw = context.getAttribute(ATTR_RESTORED_CHECKPOINT);
        if (raw instanceof ConversationCheckpoint checkpoint) {
            return Optional.of(checkpoint);
        }
        return Optional.empty();
    }

    public SlotCollection slotsOf(InteractionContext context) {
        if (context == null) {
            return SlotCollection.empty();
        }
        Object raw = context.getAttribute(ATTR_SLOT_COLLECTION);
        if (raw instanceof SlotCollection slots) {
            return slots;
        }
        return SlotCollection.empty();
    }

    public void putSlots(InteractionContext context, SlotCollection slots) {
        if (context != null) {
            context.putAttribute(ATTR_SLOT_COLLECTION, slots != null ? slots : SlotCollection.empty());
        }
    }

    private Long currentTenantId() {
        return TenantContext.getCurrentTenantId();
    }
}
