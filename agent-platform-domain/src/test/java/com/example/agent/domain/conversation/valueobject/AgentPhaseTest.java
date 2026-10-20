package com.example.agent.domain.conversation.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AgentPhase 解析与可续传判定.
 */
class AgentPhaseTest {

    @Test
    void fromCode_blank_defaultsInit() {
        assertEquals(AgentPhase.INIT, AgentPhase.fromCode(null));
        assertEquals(AgentPhase.INIT, AgentPhase.fromCode(""));
        assertEquals(AgentPhase.INIT, AgentPhase.fromCode("  "));
    }

    @Test
    void fromCode_ignoreCase() {
        assertEquals(AgentPhase.AWAIT_PARAM, AgentPhase.fromCode("await_param"));
        assertEquals(AgentPhase.EXECUTING, AgentPhase.fromCode("EXECUTING"));
    }

    @Test
    void fromCode_unknown_throws() {
        assertThrows(IllegalArgumentException.class, () -> AgentPhase.fromCode("NOT_A_PHASE"));
    }

    @Test
    void resumable_onlyInProgressPhases() {
        assertTrue(AgentPhase.AWAIT_PARAM.isResumable());
        assertTrue(AgentPhase.EXECUTING.isResumable());
        assertTrue(AgentPhase.CONFIRMATION.isResumable());
        assertFalse(AgentPhase.INIT.isResumable());
        assertFalse(AgentPhase.DONE.isResumable());
        assertFalse(AgentPhase.ERROR.isResumable());
    }

    @Test
    void canTransit_awaitParamToExecuting() {
        assertTrue(AgentPhase.AWAIT_PARAM.canTransitTo(AgentPhase.EXECUTING));
        assertFalse(AgentPhase.INIT.canTransitTo(AgentPhase.DONE));
    }
}
