package com.example.agent.infrastructure.config.websocket;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * WebSocket 配置 — 注册 Handler 与认证拦截器.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ConversationWebSocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/conversation")
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketAuthInterceptor());
    }

    /**
     * WebSocket 握手认证拦截器 — 校验 Bearer Token.
     */
    public static class WebSocketAuthInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                        WebSocketHandler wsHandler, Map<String, Object> attributes) {
            List<String> authHeaders = request.getHeaders().get("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                log.warn("[WebSocket] 握手缺少 Authorization header");
                return false;
            }
            String token = authHeaders.get(0).replace("Bearer ", "");
            try {
                Object loginId = StpUtil.getLoginIdByToken(token);
                attributes.put("userId", loginId.toString());
                return true;
            } catch (Exception e) {
                log.warn("[WebSocket] Token 校验失败: {}", e.getMessage());
                return false;
            }
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Exception exception) {
        }
    }
}
