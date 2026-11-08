package com.example.agent.application.im;

import com.example.agent.application.conversation.ConversationApplicationService;
import com.example.agent.application.conversation.ConversationApplicationService.CreateConversationRequest;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.conversation.StreamPipelineGuard;
import com.example.agent.application.im.dto.ImCallbackCommand;
import com.example.agent.application.im.dto.ImCallbackResponse;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.im.model.IncomingImMessage;
import com.example.agent.domain.im.model.UnifiedMessage;
import com.example.agent.domain.im.service.ImAdapter;
import com.example.agent.domain.im.valueobject.ImChannel;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * IM 回调编排：渠道转换 → 围栏 → 会话落库 → LLM 回复 → 回写渠道。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImApplicationService {

    private final ImAdapterRegistry adapterRegistry;
    private final ConversationApplicationService conversationService;
    private final MessageApplicationService messageService;
    private final StreamPipelineGuard pipelineGuard;
    private final ChatClient chatClient;

    private final ConcurrentHashMap<String, String> conversationIndex = new ConcurrentHashMap<>();

    @Value("${app.im.callback-token:dev-im-token}")
    private String callbackToken;

    public ImCallbackResponse handleCallback(ImCallbackCommand command) {
        if (callbackToken == null || callbackToken.isBlank()
                || command.getToken() == null || !callbackToken.equals(command.getToken())) {
            throw new BusinessException(401, "IM 回调鉴权失败");
        }
        if (command.getTenantId() == null) {
            throw new BusinessException(400, "tenantId 不能为空");
        }
        if (command.getChannelUserId() == null || command.getChannelUserId().isBlank()) {
            throw new BusinessException(400, "channelUserId 不能为空");
        }
        if (command.getContent() == null || command.getContent().isBlank()) {
            throw new BusinessException(400, "消息内容不能为空");
        }

        ImChannel channel = ImChannel.fromCode(command.getChannel());
        ImAdapter adapter = adapterRegistry.require(channel);
        UnifiedMessage incoming = adapter.convert(IncomingImMessage.builder()
                .msgId(command.getMsgId())
                .channelUserId(command.getChannelUserId())
                .content(command.getContent())
                .msgType(command.getMsgType())
                .build());

        String userId = command.getUserId() != null && !command.getUserId().isBlank()
                ? command.getUserId() : ("im-" + channel.getCode() + "-" + command.getChannelUserId());

        TenantContext.setTenantId(command.getTenantId());
        TenantContext.setUserId(userId);
        try {
            pipelineGuard.filterInput(null, userId, command.getTenantId(), incoming.getContent());

            String conversationId = conversationIndex.computeIfAbsent(
                    channel.getCode() + ":" + command.getChannelUserId(),
                    key -> createConversation(channel, command.getChannelUserId()));

            messageService.saveUserMessage(conversationId, incoming.getContent());
            String reply = chatClient.prompt().user(incoming.getContent()).call().content();
            String safeReply = pipelineGuard.desensitizeOutput(conversationId, reply != null ? reply : "");
            messageService.saveAssistantMessage(conversationId, safeReply, estimateTokens(safeReply));

            adapter.sendReply(UnifiedMessage.builder()
                    .channel(channel)
                    .channelUserId(command.getChannelUserId())
                    .content(safeReply)
                    .msgType("text")
                    .build(), command.getChannelUserId());

            log.info("[IM] 回调已处理: channel={}, convId={}, user={}",
                    channel.getCode(), conversationId, command.getChannelUserId());
            return ImCallbackResponse.builder()
                    .accepted(true)
                    .conversationId(conversationId)
                    .reply(safeReply)
                    .build();
        } finally {
            TenantContext.clear();
        }
    }

    private String createConversation(ImChannel channel, String channelUserId) {
        CreateConversationRequest request = new CreateConversationRequest();
        request.setTitle("IM-" + channel.getCode() + "-" + channelUserId);
        return conversationService.createConversation(request).getConversationId();
    }

    private int estimateTokens(String content) {
        if (content == null) {
            return 0;
        }
        return (int) Math.ceil(content.length() * 0.5);
    }
}
