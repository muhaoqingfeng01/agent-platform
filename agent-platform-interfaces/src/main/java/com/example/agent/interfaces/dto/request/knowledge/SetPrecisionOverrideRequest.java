package com.example.agent.interfaces.dto.request.knowledge;

import lombok.Data;

@Data
public class SetPrecisionOverrideRequest {
    private String searchParamsOverrideJson;
    private String multiStageOverrideJson;
}
