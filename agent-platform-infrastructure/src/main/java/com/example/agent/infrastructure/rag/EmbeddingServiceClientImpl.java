package com.example.agent.infrastructure.rag;

import com.example.agent.domain.knowledge.service.EmbeddingServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Embedding 服务客户端 — 基于 Spring AI EmbeddingModel.
 * <p>
 * 自动配置根据 application.yml 中 spring.ai.openai.embedding.* 创建 EmbeddingModel Bean.
 * DeepSeek API 兼容 OpenAI 协议，因此使用 OpenAI 自动配置指向 DeepSeek 端点.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Component
public class EmbeddingServiceClientImpl implements EmbeddingServiceClient {

    private final EmbeddingModel embeddingModel;

    /**
     * 构造函数注入 EmbeddingModel.
     * <p>
     * Spring AI 自动配置会根据 spring.ai.openai.* 或 spring.ai.deepseek.* 创建 EmbeddingModel Bean.
     * 如果 Bean 不存在（Embedding 未正确配置），启动时会报错.
     */
    public EmbeddingServiceClientImpl(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        log.info("[Embedding] 客户端初始化完成, modelClass={}", embeddingModel.getClass().getSimpleName());
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            log.warn("[Embedding] 空文本跳过");
            return new float[0];
        }

        // TODO: 验证实际 API 返回的维度是否与预期一致（默认 text-embedding-3-small = 1536 维）
        EmbeddingRequest request = new EmbeddingRequest(List.of(text), null);
        EmbeddingResponse response = embeddingModel.call(request);

        if (response.getResults() == null || response.getResults().isEmpty()) {
            log.error("[Embedding] API 返回空结果: textLen={}", text.length());
            throw new IllegalStateException("Embedding API 返回空结果");
        }

        // Spring AI 1.1.x Embedding.getOutput() 返回 float[]
        // Spring AI 1.2+ 返回 List<Double> — 此处按 1.1.7 处理
        float[] result = response.getResults().get(0).getOutput();

        log.debug("[Embedding] 向量化完成: textLen={}, dim={}", text.length(), result.length);
        return result;
    }
}
