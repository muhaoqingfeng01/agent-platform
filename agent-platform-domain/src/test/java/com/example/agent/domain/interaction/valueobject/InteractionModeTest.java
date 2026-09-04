package com.example.agent.domain.interaction.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * InteractionMode 枚举解析测试.
 */
class InteractionModeTest {

    @Test
    void fromCode_taskExecution() {
        assertEquals(InteractionMode.TASK_EXECUTION, InteractionMode.fromCode("TASK_EXECUTION"));
        assertEquals(InteractionMode.TASK_EXECUTION, InteractionMode.fromCode("task_execution"));
        assertEquals("任务执行", InteractionMode.TASK_EXECUTION.getDesc());
    }

    @Test
    void fromCode_analysis() {
        assertEquals(InteractionMode.ANALYSIS, InteractionMode.fromCode("ANALYSIS"));
        assertEquals(InteractionMode.ANALYSIS, InteractionMode.fromCode("analysis"));
        assertEquals("分析推理", InteractionMode.ANALYSIS.getDesc());
    }

    @Test
    void fromCode_approval() {
        assertEquals(InteractionMode.APPROVAL, InteractionMode.fromCode("APPROVAL"));
        assertEquals(InteractionMode.APPROVAL, InteractionMode.fromCode("approval"));
        assertEquals("安全审批", InteractionMode.APPROVAL.getDesc());
    }

    @Test
    void fromCode_invalid_throwsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> InteractionMode.fromCode("NOT_A_MODE"));
        assertTrue(ex.getMessage().contains("不支持的交互模式"));
        assertTrue(ex.getMessage().contains("APPROVAL"));
    }

    @Test
    void fromCode_blank_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> InteractionMode.fromCode(""));
        assertThrows(IllegalArgumentException.class, () -> InteractionMode.fromCode(null));
    }
}
