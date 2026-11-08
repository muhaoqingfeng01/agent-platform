package com.example.agent.application.conversation;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.application.memory.LongTermMemoryService;
import com.example.agent.application.prompt.PromptApplicationService;
import com.example.agent.application.security.SecurityFenceApplicationService;
import com.example.agent.application.security.filter.FilterContext;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamPipelineGuardTest {

    @Mock
    private SecurityFenceApplicationService securityFence;
    @Mock
    private LongTermMemoryService longTermMemoryService;
    @Mock
    private PromptApplicationService promptApplicationService;
    @Mock
    private ApprovalWorkflowApplicationService approvalService;

    private StreamPipelineGuard guard;

    @BeforeEach
    void setUp() {
        guard = new StreamPipelineGuard(
                securityFence, longTermMemoryService, promptApplicationService, approvalService);
    }

    @Test
    void filterInput_delegatesToFence() {
        guard.filterInput("conv-1", "u1", 9L, "hello");
        ArgumentCaptor<FilterContext> captor = ArgumentCaptor.forClass(FilterContext.class);
        verify(securityFence).filterInput(eq("hello"), captor.capture());
        assertEquals("conv-1", captor.getValue().getConversationId());
        assertEquals("u1", captor.getValue().getUserId());
        assertEquals(9L, captor.getValue().getTenantId());
    }

    @Test
    void suggestMode_knowledgeCategory() {
        IntentResult intent = IntentResult.matched("kb_search", "知识检索", "knowledge", 0.9,
                "命中", "LOW", List.of());
        assertEquals(InteractionMode.KNOWLEDGE_SEARCH, guard.suggestMode(intent));
    }

    @Test
    void pauseIfHighRiskIntent_createsApproval() {
        IntentResult intent = IntentResult.matched("delete_user", "删用户", "admin", 0.95,
                "高风险", "HIGH", List.of());
        when(approvalService.createActionApproval(any(), anyString(), anyString(), anyString(), any()))
                .thenReturn(ApprovalWorkflowResponse.builder().approvalId("appr_1").title("审批").build());

        assertTrue(guard.pauseIfHighRiskIntent(intent, "conv-1").isPresent());
        verify(approvalService).createActionApproval(any(), eq("conv-1"), anyString(), eq("delete_user"), any());
    }

    @Test
    void pauseIfHighRiskIntent_lowRiskSkipped() {
        IntentResult intent = IntentResult.unknown("none");
        assertTrue(guard.pauseIfHighRiskIntent(intent, "conv-1").isEmpty());
        verify(approvalService, never()).createActionApproval(any(), any(), any(), any(), any());
    }

    @Test
    void assembleConversationPrompt_fallsBackWhenNoTemplate() {
        when(longTermMemoryService.loadUserMemories("u1")).thenReturn(List.of());
        when(longTermMemoryService.injectMemoriesIntoPrompt(anyString(), any())).thenAnswer(inv -> inv.getArgument(0));
        when(promptApplicationService.runtimeRenderLatestPublished(anyString(), any())).thenReturn(null);

        String prompt = guard.assembleConversationPrompt("u1", List.of(), "你好", SlotCollection.empty());
        assertTrue(prompt.contains("你好"));
    }

    @Test
    void assembleConversationPrompt_usesPublishedTemplate() {
        when(longTermMemoryService.loadUserMemories("u1")).thenReturn(List.of(LongTermMemory.builder()
                .memoryKey("name").memoryValue("张三").confidence(0.9).build()));
        when(promptApplicationService.runtimeRenderLatestPublished(anyString(), any()))
                .thenReturn("渲染后的模板");

        assertEquals("渲染后的模板",
                guard.assembleConversationPrompt("u1", List.of(), "你好", SlotCollection.empty()));
    }
}
