package com.example.agent.domain.knowledge.service;

import com.example.agent.domain.knowledge.entity.Document;

/**
 * 文本提取器端口 — 由 Infrastructure 层实现（Tika）.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@FunctionalInterface
public interface TextExtractor {
    String extractText(Document doc) throws Exception;
}
