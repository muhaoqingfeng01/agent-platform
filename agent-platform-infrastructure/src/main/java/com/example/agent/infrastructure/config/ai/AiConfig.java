package com.example.agent.infrastructure.config.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
     * 将 DeepSeek ChatModel 标记为主要使用的模型，解决与 OpenAI ChatModel 的冲突问题。
     * <p>
     * 当项目中同时存在 deepSeekChatModel 和 openAiChatModel 时，
     * Spring 无法确定注入哪一个。使用 @Primary 指定优先级。
     *
     * @param deepSeekChatModel DeepSeek ChatModel（由 spring-ai-autoconfigure-model-deepseek 自动创建）
     * @return 标记为 @Primary 的 ChatModel
     */
    @Bean
    @Primary
    public ChatModel primaryChatModel(ChatModel deepSeekChatModel) {
        return deepSeekChatModel;
    }

    /**
     * 提供全局 ChatClient Bean，供 LLMRecognizer / StreamOrchestrationService / LongTermMemoryService 注入.
     *
     * @param builder ChatClient.Builder（由 Spring AI 自动配置）
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
