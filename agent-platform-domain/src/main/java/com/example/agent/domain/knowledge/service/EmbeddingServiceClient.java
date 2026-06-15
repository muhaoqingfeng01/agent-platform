package com.example.agent.domain.knowledge.service;

/**
 * Embedding 服务客户端端口 — 由 Infrastructure 层实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@FunctionalInterface
public interface EmbeddingServiceClient {
    float[] embed(String text);
}
