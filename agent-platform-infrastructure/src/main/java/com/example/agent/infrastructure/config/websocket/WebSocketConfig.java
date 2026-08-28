package com.example.agent.infrastructure.config.websocket;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.common.constant.ProjectConstants;
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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        registry.addHandler(handler, ProjectConstants.WebSocket.CONVERSATION_ENDPOINT)
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
            String token = extractToken(request);
            if (token == null || token.isBlank()) {
                log.warn("[WebSocket] 握手缺少认证凭据（Authorization header / ?token= 均缺失）");
                return false;
            }
            try {
                Object loginId = StpUtil.getLoginIdByToken(token);
                attributes.put("userId", loginId.toString());
                return true;
            } catch (Exception e) {
                log.warn("[WebSocket] Token 校验失败: {}", e.getMessage());
                return false;
            }
        }

        /**
         * 提取 Token — 优先读取 {@code Authorization: Bearer <token>} 头；
         * 浏览器原生 WebSocket 无法自定义 Header，此时回退到查询参数 {@code ?token=<token>}。
         */
        private String extractToken(ServerHttpRequest request) {
            List<String> authHeaders = request.getHeaders().get("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String header = authHeaders.get(0);
                if (header != null && header.startsWith("Bearer ")) {
                    return header.substring("Bearer ".length()).trim();
                }
            }
            String query = request.getURI().getQuery();
            if (query != null) {
                for (String pair : query.split("&")) {
                    int idx = pair.indexOf('=');
                    if (idx > 0 && "token".equals(pair.substring(0, idx))) {
                        return URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    }
                }
            }
            return null;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Exception exception) {
        }
    }
}
