package com.example.agent.application.conversation;

import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.conversation.entity.Conversation;
import com.example.agent.domain.conversation.repository.ConversationRepository;
import com.example.agent.domain.conversation.service.ConversationDomainService;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话应用服务 — Facade + Template Method 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationApplicationService {

    private final ConversationRepository conversationRepository;
    private final ConversationDomainService domainService;

    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        Conversation conversation = Conversation.builder()
                .conversationId(IdGenerator.generate("conv"))
                .tenantId(TenantContext.getCurrentTenantId())
                .agentId(request.getAgentId())
                .userId(TenantContext.getCurrentUserId())
                .title(request.getTitle() != null ? request.getTitle() : "新对话")
                .status(ConversationStatus.ACTIVE)
                .messageCount(0)
                .totalTokens(0L)
                .metadata(request.getMetadata() != null ? request.getMetadata() : new HashMap<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        domainService.validateNewConversation(conversation);
        conversationRepository.save(conversation);
        log.info("[Conversation] 创建: convId={}, userId={}", conversation.getConversationId(), conversation.getUserId());
        return ConversationResponse.from(conversation);
    }

    public Conversation getConversation(String conversationId) {
        Conversation conv = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("会话", conversationId));
        domainService.assertAccess(conv, TenantContext.getCurrentTenantId(), TenantContext.getCurrentUserId());
        return conv;
    }

    public PageResponse<ConversationResponse> listConversations(int page, int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String userId = TenantContext.getCurrentUserId();
        List<Conversation> conversations = conversationRepository.findByUserId(tenantId, userId, page, size);
        long total = conversationRepository.countByUserId(tenantId, userId);
        return PageResponse.of(conversations.stream().map(ConversationResponse::from).toList(), total, page, size);
    }

    @Transactional
    public void updateTitle(String conversationId, String title) {
        Conversation conv = getConversation(conversationId);
        conv.updateTitle(title);
        conversationRepository.updateTitle(conversationId, title);
    }

    @Transactional
    public void transitionStatus(String conversationId, ConversationStatus targetStatus) {
        Conversation conv = getConversation(conversationId);
        switch (targetStatus) {
            case CLOSED -> conv.close();
            case ACTIVE -> conv.reopen();
            case ARCHIVED -> conv.archive();
            default -> throw new IllegalArgumentException("不支持的目标状态: " + targetStatus);
        }
        conversationRepository.updateStatus(conversationId, targetStatus);
        log.info("[Conversation] 状态变更: convId={}, status={}", conversationId, targetStatus);
    }

    @Transactional
    public void softDelete(String conversationId) {
        getConversation(conversationId);
        conversationRepository.softDelete(conversationId);
    }

    // ==================== DTOs ====================

    @Data
    public static class CreateConversationRequest {
        private String agentId;
        private String title;
        private Map<String, Object> metadata;
    }

    @Data
    @Builder
    public static class ConversationResponse {
        private String conversationId;
        private String agentId;
        private String userId;
        private String title;
        private String status;
        private int messageCount;
        private long totalTokens;
        private Long createdAt;

        public static ConversationResponse from(Conversation conv) {
            return ConversationResponse.builder()
                    .conversationId(conv.getConversationId())
                    .agentId(conv.getAgentId())
                    .userId(conv.getUserId())
                    .title(conv.getTitle())
                    .status(conv.getStatus().name())
                    .messageCount(conv.getMessageCount())
                    .totalTokens(conv.getTotalTokens())
                    .createdAt(TimeConverters.toEpochMilli(conv.getCreatedAt()))
                    .build();
        }
    }
}
