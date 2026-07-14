package com.example.agent.infrastructure.config.ai;

import com.example.agent.infrastructure.config.nacos.AiModelConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring AI 配置 — 提供 ChatClient Bean.
 * <p>
 * Spring AI 默认只自动配置 {@link ChatClient.Builder}，ChatClient 本身需要手动声明为 Bean。
 * <p>🆕 P6 配置治理子方案03: 注入 AiModelConfig，ChatClient 默认参数从 Nacos 动态读取.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Configuration
public class AiConfig {

    private final AiModelConfig aiModelConfig;

    public AiConfig(AiModelConfig aiModelConfig) {
        this.aiModelConfig = aiModelConfig;
    }

    /**
     * 将 DeepSeek ChatModel 标记为主要使用的模型，解决与 OpenAI ChatModel 的冲突问题。
     */
    @Bean
    @Primary
    public ChatModel primaryChatModel(ChatModel deepSeekChatModel) {
        return deepSeekChatModel;
    }

    /**
     * 提供全局 ChatClient Bean，默认选项从 AiModelConfig（Nacos 动态配置）读取.
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(aiModelConfig.getChatModel())
                        .temperature(aiModelConfig.getChatTemperature())
                        .maxTokens(aiModelConfig.getChatMaxTokens())
                        .build())
                .build();
    }
}
