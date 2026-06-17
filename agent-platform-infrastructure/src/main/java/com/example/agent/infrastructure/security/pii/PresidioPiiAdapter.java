package com.example.agent.infrastructure.security.pii;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Microsoft Presidio PII 分析适配器.
 *
 * <p>通过 HTTP 调用 Presidio Analyzer 服务进行高级 PII 检测.
 * 条件装配: security.pii.presidio.enabled=true 时启用.
 *
 * <p>Presidio 基于 NER（命名实体识别）模型，可识别人名、地址、公司名等
 * 正则方案无法覆盖的 PII 类型。不可用时静默降级到正则模式.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "security.pii.presidio.enabled", havingValue = "true")
public class PresidioPiiAdapter {

    private final RestClient restClient;

    /**
     * 调用 Presidio Analyzer 分析文本中的 PII.
     *
     * @param text     待检测文本
     * @param language 语言（zh/en）
     * @return PII 检测结果列表，调用失败时返回空列表
     */
    public List<PiiDetection> analyze(String text, String language) {
        try {
            AnalyzeRequest request = AnalyzeRequest.builder()
                .text(text)
                .language(language)
                .build();

            ResponseEntity<List<PiiDetection>> response = restClient.post()
                .uri("/analyze")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

            List<PiiDetection> detections = response.getBody() != null ? response.getBody() : List.of();
            log.debug("[Presidio] 检测到 {} 个 PII 实体", detections.size());
            return detections;

        } catch (Exception e) {
            log.error("[Presidio] 调用失败，回退到正则脱敏: {}", e.getMessage());
            return List.of();  // 静默降级，不阻塞请求
        }
    }

    // --- DTO ---

    @Data
    @Builder
    public static class AnalyzeRequest {
        private String text;
        private String language;
    }

    @Data
    public static class PiiDetection {
        @JsonProperty("entity_type")
        private String entityType;      // PHONE_NUMBER / PERSON / LOCATION ...

        @JsonProperty("start")
        private int start;

        @JsonProperty("end")
        private int end;

        @JsonProperty("score")
        private float score;            // 置信度 0-1
    }
}
