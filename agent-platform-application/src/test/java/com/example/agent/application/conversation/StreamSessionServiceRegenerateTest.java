package com.example.agent.application.conversation;

import com.example.agent.application.interaction.InteractionApplicationService;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.repository.MessageRepository;
import com.example.agent.domain.conversation.valueobject.MessageRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * 重生用户内容解析测试.
 */
@ExtendWith(MockitoExtension.class)
class StreamSessionServiceRegenerateTest {

    @Mock
    private StreamRegistry streamRegistry;
    @Mock
    private InteractionApplicationService interactionService;
    @Mock
    private MessageRepository messageRepository;

    private StreamSessionService service;

    @BeforeEach
    void setUp() {
        service = new StreamSessionService(streamRegistry, interactionService, messageRepository);
    }

    @Test
    void resolveUserContent_success() {
        Message assistant = Message.builder()
                .messageId("msg-a")
                .conversationId("conv-1")
                .role(MessageRole.ASSISTANT)
                .content("旧回复")
                .build();
        Message user = Message.builder()
                .messageId("msg-u")
                .conversationId("conv-1")
                .role(MessageRole.USER)
                .content("请解释一下")
                .build();
        when(messageRepository.findByMessageId("msg-a")).thenReturn(Optional.of(assistant));
        when(messageRepository.findPreviousUserMessage("conv-1", "msg-a")).thenReturn(Optional.of(user));

        assertEquals("请解释一下", service.resolveUserContentForRegenerate("conv-1", "msg-a"));
    }

    @Test
    void resolveUserContent_rejectsUserMessage() {
        Message user = Message.builder()
                .messageId("msg-u")
                .conversationId("conv-1")
                .role(MessageRole.USER)
                .content("你好")
                .build();
        when(messageRepository.findByMessageId("msg-u")).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> service.resolveUserContentForRegenerate("conv-1", "msg-u"));
    }

    @Test
    void resolveUserContent_missingSource_throws404() {
        when(messageRepository.findByMessageId("missing")).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resolveUserContentForRegenerate("conv-1", "missing"));
        assertEquals(404, ex.getCode());
    }
}
