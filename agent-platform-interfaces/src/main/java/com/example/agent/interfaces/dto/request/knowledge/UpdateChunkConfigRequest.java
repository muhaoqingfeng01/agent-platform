package com.example.agent.interfaces.dto.request.knowledge;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateChunkConfigRequest {
    private String defaultChunkStrategy;
    private String chunkConfigJson;
    private Map<String, Object> chunkConfig;
}
