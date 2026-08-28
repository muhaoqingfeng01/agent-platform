package com.example.agent.application.interaction.strategy;

import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.analytics.AnalyticsQueryPort;
import com.example.agent.domain.analytics.AnalyticsQueryPort.EvalOverallResult;
import com.example.agent.domain.analytics.AnalyticsQueryPort.FeedbackDistResult;
import com.example.agent.domain.analytics.AnalyticsQueryPort.HitRateResult;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 分析推理交互策略测试 — 时间解析与无数据时不调用 LLM.
 */
@ExtendWith(MockitoExtension.class)
class AnalysisInteractionStrategyTest {

    @Mock
    private AnalyticsQueryPort analyticsQueryPort;
    @Mock
    private MessageApplicationService messageService;
    @Mock
    private ChatClient chatClient;

    private AnalysisInteractionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new AnalysisInteractionStrategy(
                analyticsQueryPort, messageService, chatClient, null);
    }

    @Test
    void getMode_isAnalysis() {
        assertEquals(InteractionMode.ANALYSIS, strategy.getMode());
    }

    @Test
    void execute_sync_rejected() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> strategy.execute(InteractionContext.forAnalysis(
                        "最近7天命中率", "conv_1", 1L, "u1", new SseEmitter(),
                        Map.of("canObservability", true, "canEvaluation", true))));
        assertEquals(400, ex.getCode());
    }

    @Test
    void parseDays_supports7And30() {
        assertEquals(7, strategy.parseDays("本周知识库命中率趋势（最近7天）"));
        assertEquals(7, strategy.parseDays("近 7 天反馈分布"));
        assertEquals(30, strategy.parseDays("最近30天评估总体"));
        assertEquals(30, strategy.parseDays("本月 BadCase 分布"));
        assertNull(strategy.parseDays("昨天的命中率"));
        assertNull(strategy.parseDays("分析一下平台数据"));
    }

    @Test
    void executeStream_unrecognizedRange_doesNotQuery() {
        when(messageService.saveUserMessage(anyString(), anyString()))
                .thenReturn(Message.builder().messageId("msg_user").build());
        when(messageService.saveAssistantMessage(anyString(), anyString(), anyInt()))
                .thenReturn(Message.builder().messageId("msg_assistant").build());

        SseEmitter emitter = new SseEmitter(1_000L);
        InteractionContext context = InteractionContext.forAnalysis(
                "分析一下平台数据", "conv_1", 1L, "u1", emitter,
                Map.of("canObservability", true, "canEvaluation", true));

        strategy.executeStream(context);

        verify(analyticsQueryPort, never()).hitRate(any(), any(), any());
        verify(analyticsQueryPort, never()).feedbackDist(any(), any(), any());
        verify(analyticsQueryPort, never()).evalOverall(any(), any(), any());
        verify(chatClient, never()).prompt();
    }

    @Test
    void executeStream_noData_doesNotCallLlm() {
        when(messageService.saveUserMessage(anyString(), anyString()))
                .thenReturn(Message.builder().messageId("msg_user").build());
        when(messageService.saveAssistantMessage(anyString(), anyString(), anyInt()))
                .thenReturn(Message.builder().messageId("msg_assistant").build());
        when(analyticsQueryPort.hitRate(any(), any(), any()))
                .thenReturn(new HitRateResult(0, 0, null, List.of()));
        when(analyticsQueryPort.feedbackDist(any(), any(), any()))
                .thenReturn(new FeedbackDistResult(0, List.of()));
        when(analyticsQueryPort.evalOverall(any(), any(), any()))
                .thenReturn(new EvalOverallResult(0, 0, 0, null));

        SseEmitter emitter = new SseEmitter(1_000L);
        InteractionContext context = InteractionContext.forAnalysis(
                "最近7天平台数据总览", "conv_1", 1L, "u1", emitter,
                Map.of("canObservability", true, "canEvaluation", true));

        strategy.executeStream(context);

        verify(analyticsQueryPort).hitRate(any(), any(), any());
        verify(chatClient, never()).prompt();
    }
}
