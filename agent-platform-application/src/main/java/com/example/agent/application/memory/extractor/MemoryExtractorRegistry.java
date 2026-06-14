package com.example.agent.application.memory.extractor;

import com.example.agent.domain.conversation.valueobject.MemoryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 记忆提取器注册中心 — Factory 模式自动装配.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class MemoryExtractorRegistry {

    private final Map<MemoryType, MemoryExtractor> extractorMap;

    public MemoryExtractorRegistry(List<MemoryExtractor> extractors) {
        this.extractorMap = extractors.stream()
                .collect(Collectors.toMap(MemoryExtractor::supportedType, Function.identity()));
        log.info("[LTM] 注册记忆提取器: {}", extractorMap.keySet());
    }

    public MemoryExtractor get(MemoryType type) {
        return extractorMap.getOrDefault(type, extractorMap.get(MemoryType.FACT));
    }
}
