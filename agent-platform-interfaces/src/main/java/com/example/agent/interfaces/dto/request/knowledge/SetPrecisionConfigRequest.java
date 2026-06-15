package com.example.agent.interfaces.dto.request.knowledge;

import lombok.Data;

import java.util.Map;

@Data
public class SetPrecisionConfigRequest {
    private String indexType;
    private Map<String, Object> indexParams;
    private String searchStrategy;
    private Map<String, Object> searchParams;
    private Map<String, Object> multiStageParams;
    private Map<String, Object> monitoringParams;
}
