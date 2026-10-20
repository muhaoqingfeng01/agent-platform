package com.example.agent.domain.conversation.valueobject;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 槽位补全与续传文案.
 */
class SlotCollectionTest {

    @Test
    void fromRequiredParams_fillNext_untilComplete() {
        SlotCollection slots = SlotCollection.fromRequiredParamMaps(List.of(
                Map.of("name", "orderId", "description", "订单号"),
                Map.of("name", "date", "description", "日期")
        ));
        assertFalse(slots.isComplete());
        assertEquals("请继续补充订单号", slots.continuePrompt());

        SlotCollection afterFirst = slots.fillNext("A1001");
        assertFalse(afterFirst.isComplete());
        assertEquals("请继续补充日期", afterFirst.continuePrompt());
        assertEquals("A1001", afterFirst.getFilled().get("orderId"));
        assertFalse(slots.getFilled().containsKey("orderId"));

        SlotCollection done = afterFirst.fillNext("2026-09-04");
        assertTrue(done.isComplete());
        assertEquals("2026-09-04", done.getFilled().get("date"));
    }

    @Test
    void fillNext_blank_doesNotFill() {
        SlotCollection slots = SlotCollection.fromRequiredParamMaps(List.of(
                Map.of("name", "city", "description", "城市")
        ));
        assertEquals(slots, slots.fillNext("  "));
        assertFalse(slots.isComplete());
    }

    @Test
    void emptyRequired_isComplete() {
        assertTrue(SlotCollection.empty().isComplete());
        assertFalse(SlotCollection.empty().hasRequired());
    }

    @Test
    void fromPayload_roundTrip() {
        SlotCollection original = SlotCollection.fromRequiredParamMaps(List.of(
                Map.of("key", "sku", "label", "商品编码")
        )).fillNext("SKU-9");
        SlotCollection restored = SlotCollection.fromPayload(original.toPayload());
        assertEquals("SKU-9", restored.getFilled().get("sku"));
        assertEquals("请继续补充商品编码", SlotCollection.fromRequiredParamMaps(
                List.of(Map.of("key", "sku", "label", "商品编码"))).continuePrompt());
        assertTrue(restored.isComplete());
    }
}
