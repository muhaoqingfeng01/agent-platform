package com.example.agent.infrastructure.config.websocket;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理器 — Mediator 模式集中管理所有连接.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class ConversationWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        if (userId != null) {
            WebSocketSession oldSession = sessions.put(userId, session);
            closeQuietly(oldSession);
            log.info("[WebSocket] 连接建立: userId={}, sessionId={}", userId, session.getId());
        } else {
            closeQuietly(session);
            log.warn("[WebSocket] 无法识别用户，关闭连接");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        if (userId != null) {
            sessions.remove(userId, session);
            log.info("[WebSocket] 连接关闭: userId={}, status={}", userId, status);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if ("ping".equals(payload)) {
            sendMessage(session, new TextMessage("pong"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("[WebSocket] 传输错误: userId={}", extractUserId(session), exception);
        closeQuietly(session);
    }

    public void pushMessage(String userId, WebSocketMessage message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error("[WebSocket] 推送失败: userId={}", userId, e);
                sessions.remove(userId);
            }
        }
    }

    public void broadcast(WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            sessions.forEach((userId, session) -> {
                if (session.isOpen()) {
                    sendMessage(session, new TextMessage(json));
                }
            });
        } catch (IOException e) {
            log.error("[WebSocket] 广播序列化失败", e);
        }
    }

    public int getOnlineCount() {
        return sessions.size();
    }

    private String extractUserId(WebSocketSession session) {
        String token = session.getHandshakeHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                return StpUtil.getLoginIdByToken(token.substring(7)).toString();
            } catch (Exception e) {
                log.warn("[WebSocket] Token 提取失败: {}", e.getMessage());
            }
        }
        return null;
    }

    private void closeQuietly(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (IOException ignored) {
        }
    }
}
