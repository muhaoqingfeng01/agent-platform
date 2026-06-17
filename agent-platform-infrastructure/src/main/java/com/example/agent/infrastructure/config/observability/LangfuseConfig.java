package com.example.agent.infrastructure.config.observability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Langfuse 可观测性配置 — 通过 HTTP Ingestion API 直接集成.
 *
 * <p>仅在 {@code langfuse.enabled=true} 时装配相关 Bean。
 * 使用 RestTemplate + Basic Auth 调用 Langfuse Ingestion API，
 * 不依赖 langfuse-java SDK（避免 auto-generated 客户端兼容性问题）。
 *
 * <h3>配置项（application.yml）</h3>
 * <pre>
 * langfuse:
 *   host: ${LANGFUSE_HOST:http://localhost:3000}
 *   public-key: ${LANGFUSE_PUBLIC_KEY:}
 *   secret-key: ${LANGFUSE_SECRET_KEY:}
 *   enabled: ${LANGFUSE_ENABLED:false}
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class LangfuseConfig {

    private final String host;
    private final String publicKey;
    private final String secretKey;

    public LangfuseConfig(@Value("${langfuse.host}") String host,
                          @Value("${langfuse.public-key}") String publicKey,
                          @Value("${langfuse.secret-key}") String secretKey) {
        this.host = host;
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    /**
     * Langfuse 专用 RestTemplate（短超时 + Basic Auth 拦截器）.
     */
    @Bean
    @ConditionalOnProperty(name = "langfuse.enabled", havingValue = "true")
    public RestTemplate langfuseRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);   // 3s 连接超时
        factory.setReadTimeout(5000);      // 5s 读取超时

        RestTemplate restTemplate = new RestTemplate(factory);
        // Basic Auth 拦截器
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String auth = java.util.Base64.getEncoder()
                    .encodeToString((publicKey + ":" + secretKey).getBytes());
            request.getHeaders().setBasicAuth(auth);
            return execution.execute(request, body);
        });

        log.info("[Langfuse] RestTemplate 已配置: host={}", host);
        return restTemplate;
    }

    public String getHost() {
        return host;
    }
}
