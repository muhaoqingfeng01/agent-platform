package com.example.agent.application.interaction.strategy;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 安全审批交互策略测试 — 无 ID 不得改库、意图解析.
 */
@ExtendWith(MockitoExtension.class)
class ApprovalInteractionStrategyTest {

    @Mock
    private ApprovalWorkflowApplicationService approvalService;
    @Mock
    private MessageApplicationService messageService;

    private ApprovalInteractionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ApprovalInteractionStrategy(approvalService, messageService, null);
    }

    @Test
    void getMode_isApproval() {
        assertEquals(InteractionMode.APPROVAL, strategy.getMode());
    }

    @Test
    void parseIntent_approveWithId() {
        var intent = strategy.parseIntent("同意 appr_abc123");
        assertEquals(ApprovalInteractionStrategy.Action.APPROVE, intent.action());
        assertEquals("appr_abc123", intent.approvalId());
    }

    @Test
    void parseIntent_rejectWithIdAndReason() {
        var intent = strategy.parseIntent("拒绝 appr_xyz99 风险过高");
        assertEquals(ApprovalInteractionStrategy.Action.REJECT, intent.action());
        assertEquals("appr_xyz99", intent.approvalId());
    }

    @Test
    void parseIntent_approveWithoutId() {
        var intent = strategy.parseIntent("同意");
        assertEquals(ApprovalInteractionStrategy.Action.APPROVE, intent.action());
        assertNull(intent.approvalId());
    }

    @Test
    void parseIntent_list() {
        var intent = strategy.parseIntent("查看待办");
        assertEquals(ApprovalInteractionStrategy.Action.LIST, intent.action());
    }

    @Test
    void extractApprovalId() {
        assertEquals("appr_demo01", strategy.extractApprovalId("请批准 appr_demo01 谢谢"));
        assertNull(strategy.extractApprovalId("同意刚才那笔"));
    }

    @Test
    void executeStream_approveWithoutId_doesNotMutate() {
        when(messageService.saveUserMessage(anyString(), anyString()))
                .thenReturn(Message.builder().messageId("msg_user").build());
        when(messageService.saveAssistantMessage(anyString(), anyString(), anyInt()))
                .thenReturn(Message.builder().messageId("msg_assistant").build());
        when(approvalService.listPending(eq("conv_1")))
                .thenReturn(List.of(ApprovalWorkflowResponse.builder()
                        .approvalId("appr_first")
                        .title("高风险工具调用")
                        .status("PENDING")
                        .build()));

        SseEmitter emitter = new SseEmitter(1_000L);
        InteractionContext context = InteractionContext.forApproval(
                "同意", "conv_1", 1L, "u1", emitter,
                Map.of("canRead", true, "canApprove", true));

        strategy.executeStream(context);

        verify(approvalService, never()).approve(anyString(), anyString());
        verify(approvalService, never()).reject(anyString(), anyString());
        verify(approvalService).listPending("conv_1");
    }

    @Test
    void execute_approveWithId_callsService() {
        when(approvalService.approve(eq("appr_ok1"), anyString()))
                .thenReturn(ApprovalWorkflowResponse.builder()
                        .approvalId("appr_ok1")
                        .title("测试工单")
                        .status("APPROVED")
                        .build());

        InteractionContext context = InteractionContext.forApproval(
                "同意 appr_ok1", "conv_1", 1L, "u1", null,
                Map.of("canRead", true, "canApprove", true));

        Object result = strategy.execute(context);
        assertFalse(result == null);
        verify(approvalService).approve(eq("appr_ok1"), anyString());
        verify(approvalService, never()).listPending(isNull());
    }
}
