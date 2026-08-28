package com.example.agent.application.interaction.strategy;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.task.DagExecutionService;
import com.example.agent.application.task.TaskPlanningService;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.domain.task.repository.TaskStepExecutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 任务执行交互策略测试 — 规划失败不得启动 DAG，也不得留下 FAILED 执行.
 */
@ExtendWith(MockitoExtension.class)
class TaskExecutionInteractionStrategyTest {

    @Mock
    private TaskPlanningService planningService;
    @Mock
    private DagExecutionService dagExecutionService;
    @Mock
    private ApprovalWorkflowApplicationService approvalService;
    @Mock
    private MessageApplicationService messageService;
    @Mock
    private TaskStepExecutionRepository stepExecutionRepository;

    private TaskExecutionInteractionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TaskExecutionInteractionStrategy(
                planningService, dagExecutionService, approvalService,
                messageService, stepExecutionRepository, null);
    }

    @Test
    void getMode_isTaskExecution() {
        assertEquals(InteractionMode.TASK_EXECUTION, strategy.getMode());
    }

    @Test
    void execute_sync_rejected() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> strategy.execute(InteractionContext.forTaskExecution(
                        "查订单", "conv_1", 1L, "u1", new SseEmitter())));
        assertEquals(400, ex.getCode());
    }

    @Test
    void executeStream_planFailure_doesNotExecuteOrLeaveFailedTask() {
        when(messageService.saveUserMessage(anyString(), anyString()))
                .thenReturn(Message.builder().messageId("msg_user").build());
        when(messageService.saveAssistantMessage(anyString(), anyString(), anyInt()))
                .thenReturn(Message.builder().messageId("msg_assistant").build());
        when(planningService.plan(any())).thenThrow(new BusinessException(400, "DAG 存在循环依赖"));

        SseEmitter emitter = new SseEmitter(1_000L);
        InteractionContext context = InteractionContext.forTaskExecution(
                "帮我查订单并汇总", "conv_1", 1L, "u1", emitter);

        strategy.executeStream(context);

        verify(dagExecutionService, never()).executeAsync(any(), any(), any(), any());
        verify(dagExecutionService, never()).execute(any(), any(), any());
        verify(dagExecutionService, never()).cancel(any());
        verify(approvalService, never()).createActionApproval(any(), any(), any(), any(), any());
    }
}
