package com.example.agent.application.conversation;

import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.repository.ConversationRepository;
import com.example.agent.domain.conversation.repository.MessageRepository;
import com.example.agent.domain.conversation.valueobject.FeedbackType;
import com.example.agent.domain.conversation.valueobject.MessageRole;
import com.example.agent.application.memory.SessionMemoryService;
import com.example.agent.application.memory.LongTermMemoryService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息应用服务 — Template Method 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageApplicationService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SessionMemoryService sessionMemoryService;
    private final LongTermMemoryService longTermMemoryService;

    @Transactional
    public Message saveUserMessage(String conversationId, String content) {
        return saveMessage(conversationId, MessageRole.USER, content, new HashMap<>());
    }

    @Transactional
    public Message saveAssistantMessage(String conversationId, String content, int tokenCount) {
        return saveMessage(conversationId, MessageRole.ASSISTANT, content, Map.of("tokenCount", tokenCount));
    }

    public PageResponse<MessageResponse> listMessages(String conversationId, int page, int size) {
        int offset = page * size;
        List<Message> messages = messageRepository.findByConversationId(conversationId, offset, size);
        return PageResponse.of(messages.stream().map(MessageResponse::from).toList(), messages.size(), page, size);
    }

    public List<MessageResponse> loadMessagesBefore(String conversationId, String beforeMessageId, int size) {
        return messageRepository.findBefore(conversationId, beforeMessageId, size)
                .stream().map(MessageResponse::from).toList();
    }

    @Transactional
    public void updateFeedback(String messageId, FeedbackType feedback) {
        messageRepository.updateFeedback(messageId, feedback);
    }

    @Async
    public void extractLongTermMemoryAsync(String conversationId, String userId, String tenantId) {
        longTermMemoryService.extractAndSave(conversationId, userId, tenantId);
    }

    private Message saveMessage(String conversationId, MessageRole role, String content,
                                 Map<String, Object> metadata) {
        Message message = Message.builder()
                .messageId(IdGenerator.generate("msg"))
                .conversationId(conversationId)
                .role(role)
                .content(content)
                .tokenCount(estimateTokens(content))
                .metadata(metadata != null ? metadata : new HashMap<>())
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(message);
        conversationRepository.incrementMessageCount(conversationId, 1);
        conversationRepository.addTokens(conversationId, message.getTokenCount());
        sessionMemoryService.appendMessage(conversationId, message);
        return message;
    }

    private int estimateTokens(String content) {
        if (content == null) return 0;
        return (int) Math.ceil(content.length() * 0.5);
    }

    // ==================== DTOs ====================

    @Data
    @Builder
    public static class MessageResponse {
        private String messageId;
        private String conversationId;
        private String role;
        private String content;
        private Integer tokenCount;
        private String feedback;
        private LocalDateTime createdAt;

        public static MessageResponse from(Message msg) {
            return MessageResponse.builder()
                    .messageId(msg.getMessageId())
                    .conversationId(msg.getConversationId())
                    .role(msg.getRole().name())
                    .content(msg.getContent())
                    .tokenCount(msg.getTokenCount())
                    .feedback(msg.getFeedback() != null ? msg.getFeedback().name() : null)
                    .createdAt(msg.getCreatedAt())
                    .build();
        }
    }
}
