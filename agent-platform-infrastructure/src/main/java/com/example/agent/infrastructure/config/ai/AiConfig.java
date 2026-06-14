package com.example.agent.infrastructure.config.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置 — 提供 ChatClient Bean.
 * <p>
 * Spring AI 默认只自动配置 {@link ChatClient.Builder}，ChatClient 本身需要手动声明为 Bean。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Configuration
public class AiConfig {

    /**
     * 提供全局 ChatClient Bean，供 LLMRecognizer / StreamOrchestrationService / LongTermMemoryService 注入.
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
