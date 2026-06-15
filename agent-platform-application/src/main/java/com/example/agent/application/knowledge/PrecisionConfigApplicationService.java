package com.example.agent.application.knowledge;

import com.example.agent.application.knowledge.dto.PrecisionConfigDTO;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.repository.KnowledgeBaseRepository;
import com.example.agent.domain.knowledge.service.KnowledgeBaseDomainService;
import com.example.agent.domain.knowledge.service.PrecisionConfigDomainService;
import com.example.agent.domain.knowledge.valueobject.IndexType;
import com.example.agent.domain.knowledge.valueobject.SearchStrategy;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 精度配置应用服务 — 四级覆盖策略管理.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrecisionConfigApplicationService {

    private final KnowledgeBaseRepository kbRepository;
    private final KnowledgeBaseDomainService domainService;
    private final PrecisionConfigDomainService precisionDomainService;

    /**
     * 设置知识库精度参数.
     */
    @Transactional
    public void setPrecisionConfig(String knowledgeId, PrecisionConfigDTO config) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());

        // 更新索引参数
        if (config.getIndexType() != null) {
            kbRepository.updateIndexConfig(knowledgeId,
                    IndexType.fromCode(config.getIndexType()),
                    config.getIndexParams() != null ? toJson(config.getIndexParams()) : null);
        }

        // 更新检索参数
        if (config.getSearchStrategy() != null || config.getSearchParams() != null) {
            kbRepository.updateSearchConfig(knowledgeId,
                    config.getSearchStrategy() != null ? SearchStrategy.fromCode(config.getSearchStrategy()) : null,
                    config.getSearchParams() != null ? toJson(config.getSearchParams()) : null,
                    config.getMultiStageParams() != null ? toJson(config.getMultiStageParams()) : null,
                    config.getMonitoringParams() != null ? toJson(config.getMonitoringParams()) : null);
        }

        log.info("[PrecisionConfig] 更新精度配置: kbId={}, strategy={}", knowledgeId, config.getSearchStrategy());
    }

    /**
     * 获取知识库当前生效的完整配置（合并后）.
     */
    public PrecisionConfigDTO getResolvedConfig(String knowledgeId) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());

        return PrecisionConfigDTO.builder()
                .indexType(kb.getIndexType() != null ? kb.getIndexType().name() : "IVF_FLAT")
                .indexParams(kb.getIndexParamsJson() != null ? parseJson(kb.getIndexParamsJson()) : new HashMap<>())
                .searchStrategy(kb.getSearchStrategy())
                .searchParams(kb.getSearchParamsJson() != null ? parseJson(kb.getSearchParamsJson()) : new HashMap<>())
                .multiStageParams(kb.getMultiStageParamsJson() != null ? parseJson(kb.getMultiStageParamsJson()) : new HashMap<>())
                .monitoringParams(kb.getMonitoringParamsJson() != null ? parseJson(kb.getMonitoringParamsJson()) : new HashMap<>())
                .build();
    }

    /**
     * 获取所有可用策略预设.
     */
    public List<Map<String, Object>> listStrategyPresets() {
        List<Map<String, Object>> presets = new ArrayList<>();
        presets.add(buildPreset("precise", "高精度 — 医疗/法律/金融场景",
                Map.of("nprobe_ratio", 0.25, "topK", 30, "similarity_threshold", 0.65,
                        "consistency_level", "STRONG", "timeout_ms", 10000)));
        presets.add(buildPreset("balanced", "平衡 — 通用知识问答（默认）",
                Map.of("nprobe_ratio", 0.10, "topK", 20, "similarity_threshold", 0.50,
                        "consistency_level", "BOUNDED", "timeout_ms", 5000)));
        presets.add(buildPreset("fast", "低延迟 — 实时对话",
                Map.of("nprobe_ratio", 0.05, "topK", 15, "similarity_threshold", 0.50,
                        "consistency_level", "EVENTUALLY", "timeout_ms", 2000)));
        presets.add(buildPreset("recall", "高召回 — 探索性搜索/研究",
                Map.of("nprobe_ratio", 0.20, "topK", 40, "similarity_threshold", 0.35,
                        "consistency_level", "BOUNDED", "timeout_ms", 8000)));
        presets.add(buildPreset("turbo", "极速 — 流式首token优化",
                Map.of("nprobe_ratio", 0.03, "topK", 10, "similarity_threshold", 0.55,
                        "consistency_level", "EVENTUALLY", "timeout_ms", 1000)));
        return presets;
    }

    private Map<String, Object> buildPreset(String name, String desc, Map<String, Object> searchParams) {
        Map<String, Object> preset = new LinkedHashMap<>();
        preset.put("strategy_name", name);
        preset.put("description", desc);
        preset.put("search_params", searchParams);
        preset.put("multi_stage_params", Map.of(
                "enable_reranker", false,
                "enable_rrf_fusion", true,
                "rrf_k", 60,
                "fusion_top_n", 5,
                "vector_weight", 0.5,
                "keyword_weight", 0.5
        ));
        return preset;
    }

    // ========== JSON 工具方法 ==========

    private String toJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return null;
        StringBuilder sb = new StringBuilder("{");
        map.forEach((k, v) -> {
            sb.append("\"").append(k).append("\":");
            if (v instanceof Number) sb.append(v);
            else if (v instanceof Boolean) sb.append(v);
            else sb.append("\"").append(v).append("\"");
            sb.append(",");
        });
        if (sb.charAt(sb.length() - 1) == ',') sb.setLength(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        // 生产环境应使用 Jackson ObjectMapper
        return map;
    }
}
