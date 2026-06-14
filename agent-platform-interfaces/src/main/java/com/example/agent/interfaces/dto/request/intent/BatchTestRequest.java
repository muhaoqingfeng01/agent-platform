package com.example.agent.interfaces.dto.request.intent;

import lombok.Data;

import java.util.List;

@Data
public class BatchTestRequest {
    private List<TestItem> items;

    @Data
    public static class TestItem {
        private String input;
        private String expectedIntentCode;
    }
}
