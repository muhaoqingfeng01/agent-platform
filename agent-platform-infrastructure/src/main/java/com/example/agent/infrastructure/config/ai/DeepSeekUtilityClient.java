package com.example.agent.infrastructure.config.ai;

import com.example.agent.infrastructure.config.nacos.AiModelConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek 工具型同步调用客户端 — 强制关闭 thinking，避免 Spring AI 1.1.7
 * 非流式反序列化被截断/超时（JsonEOFException）。
 *
 * <p>适用于长期记忆提取、结构化 JSON 抽取等后台任务；主对话仍走 ChatClient 流式链路。
 */
@Slf4j
@Component
public class DeepSeekUtilityClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final AiModelConfig aiModelConfig;

    public DeepSeekUtilityClient(
            ObjectMapper objectMapper,
            AiModelConfig aiModelConfig,
            @Value("${spring.ai.deepseek.api-key}") String apiKey,
            @Value("${spring.ai.deepseek.base-url:https://api.deepseek.com}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.aiModelConfig = aiModelConfig;

        String normalizedBase = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(Math.max(30, aiModelConfig.getChatTimeoutSeconds())));

        this.restClient = RestClient.builder()
                .baseUrl(normalizedBase)
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 非流式文本补全（thinking=disabled）.
     *
     * @param userPrompt 用户提示词
     * @param maxTokens  最大输出 token
     * @return assistant content 文本
     */
    public String completeText(String userPrompt, int maxTokens) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", aiModelConfig.getChatModel());
        body.put("messages", List.of(Map.of("role", "user", "content", userPrompt)));
        body.put("stream", false);
        body.put("max_tokens", maxTokens);
        // deepseek-v4-pro 默认开启 thinking；工具型任务必须关闭，缩短响应并避免截断
        body.put("thinking", Map.of("type", "disabled"));

        String raw = restClient.post()
                .uri("/chat/completions")
                .body(body)
                .retrieve()
                .body(String.class);

        return extractContent(raw);
    }

    private String extractContent(String raw) {
        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.isNull()) {
                throw new IllegalStateException("DeepSeek 响应缺少 choices[0].message.content");
            }
            return content.asText();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.warn("[DeepSeekUtility] 解析响应失败, preview={}", preview(raw));
            throw new IllegalStateException("DeepSeek 响应解析失败: " + e.getMessage(), e);
        }
    }

    private String preview(String raw) {
        if (raw == null) {
            return "null";
        }
        return raw.length() <= 200 ? raw : raw.substring(0, 200) + "...";
    }
}
