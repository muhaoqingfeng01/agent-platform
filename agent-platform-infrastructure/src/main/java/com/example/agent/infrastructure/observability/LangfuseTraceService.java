package com.example.agent.infrastructure.observability;

import com.example.agent.infrastructure.config.observability.LangfuseConfig;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Langfuse 追踪服务 — 通过 HTTP Ingestion API 记录 LLM 调用 + 工具调用.
 *
 * <p>使用 Langfuse Ingestion API（{@code POST /api/public/ingestion}），
 * 不依赖 langfuse-java SDK。所有请求异步发送，失败静默忽略（fail-open）。
 *
 * <p>Langfuse 未启用时（{@code langfuse.enabled=false}），
 * RestTemplate Bean 不存在，所有方法为 null-safe 空操作。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class LangfuseTraceService {

    private static final String INGESTION_PATH = "/api/public/ingestion";

    @Autowired(required = false)
    private RestTemplate langfuseRestTemplate;

    @Autowired(required = false)
    private LangfuseConfig langfuseConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 记录 LLM 调用 Trace + Generation.
     *
     * @param traceId       Trace ID（来自 MDC）
     * @param conversationId 会话 ID
     * @param model         LLM 模型名
     * @param prompt        输入 Prompt
     * @param completion    LLM 输出
     * @param durationMs    耗时（毫秒）
     * @param tokens        Token 消耗数
     */
    @Async
    public void logLLMCallAsync(String traceId, String conversationId, String model,
                                 String prompt, String completion, long durationMs, int tokens) {
        if (langfuseRestTemplate == null || langfuseConfig == null) return;

        try {
            String tenantId = TenantContext.getCurrentTenantId();
            String userId = TenantContext.getCurrentUserId();
            Instant now = Instant.now();
            Instant startTime = now.minusMillis(durationMs);

            List<Map<String, Object>> batch = new ArrayList<>();

            // Trace 事件
            Map<String, Object> traceEvent = new LinkedHashMap<>();
            traceEvent.put("id", traceId);
            traceEvent.put("type", "trace-create");
            traceEvent.put("timestamp", now.toString());
            Map<String, Object> traceBody = new LinkedHashMap<>();
            traceBody.put("name", "conversation:" + conversationId);
            traceBody.put("userId", userId != null ? userId : "anonymous");
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("tenantId", tenantId != null ? tenantId : "unknown");
            metadata.put("conversationId", conversationId);
            traceBody.put("metadata", metadata);
            traceEvent.put("body", traceBody);
            batch.add(traceEvent);

            // Generation 事件
            Map<String, Object> genEvent = new LinkedHashMap<>();
            genEvent.put("id", traceId + "-gen");
            genEvent.put("type", "generation-create");
            genEvent.put("timestamp", now.toString());
            Map<String, Object> genBody = new LinkedHashMap<>();
            genBody.put("traceId", traceId);
            genBody.put("name", "llm_call");
            genBody.put("model", model);
            genBody.put("startTime", startTime.toString());
            genBody.put("endTime", now.toString());
            genBody.put("input", truncate(prompt, 4000));
            genBody.put("output", truncate(completion, 2000));
            Map<String, Object> usage = new LinkedHashMap<>();
            usage.put("total", tokens);
            genBody.put("usage", usage);
            genEvent.put("body", genBody);
            batch.add(genEvent);

            // 发送
            sendBatch(batch);

        } catch (Exception e) {
            log.debug("[Langfuse] 记录 LLM 调用失败（已忽略）: traceId={}, error={}",
                    traceId, e.getMessage());
        }
    }

    /**
     * 记录工具调用 Span.
     */
    @Async
    public void logToolCallAsync(String traceId, String toolName, String input,
                                  String output, long durationMs) {
        if (langfuseRestTemplate == null || langfuseConfig == null) return;

        try {
            Instant now = Instant.now();
            Instant startTime = now.minusMillis(durationMs);

            List<Map<String, Object>> batch = new ArrayList<>();
            Map<String, Object> spanEvent = new LinkedHashMap<>();
            spanEvent.put("id", traceId + "-tool-" + UUID.randomUUID().toString().substring(0, 8));
            spanEvent.put("type", "span-create");
            spanEvent.put("timestamp", now.toString());
            Map<String, Object> spanBody = new LinkedHashMap<>();
            spanBody.put("traceId", traceId);
            spanBody.put("name", "tool:" + toolName);
            spanBody.put("startTime", startTime.toString());
            spanBody.put("endTime", now.toString());
            spanBody.put("input", truncate(input, 2000));
            spanBody.put("output", truncate(output, 2000));
            spanEvent.put("body", spanBody);
            batch.add(spanEvent);

            sendBatch(batch);

        } catch (Exception e) {
            log.debug("[Langfuse] 记录工具调用失败（已忽略）: traceId={}, tool={}, error={}",
                    traceId, toolName, e.getMessage());
        }
    }

    /**
     * 检查 Langfuse 是否已启用.
     */
    public boolean isEnabled() {
        return langfuseRestTemplate != null;
    }

    // ==================== 内部方法 ====================

    private void sendBatch(List<Map<String, Object>> batch) {
        try {
            String url = langfuseConfig.getHost() + INGESTION_PATH;
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("batch", batch);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = objectMapper.writeValueAsString(payload);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            langfuseRestTemplate.postForEntity(url, entity, String.class);

            log.trace("[Langfuse] 批量事件已发送: count={}", batch.size());
        } catch (Exception e) {
            log.debug("[Langfuse] 发送批量事件失败（已忽略）: count={}, error={}",
                    batch.size(), e.getMessage());
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return null;
        return text.length() > maxLength ? text.substring(0, maxLength) + "...[truncated]" : text;
    }
}
