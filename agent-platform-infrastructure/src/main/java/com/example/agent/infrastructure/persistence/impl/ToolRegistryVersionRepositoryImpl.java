package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tool.entity.ToolRegistryVersion;
import com.example.agent.domain.tool.repository.ToolRegistryVersionRepository;
import com.example.agent.domain.tool.valueobject.ToolType;
import com.example.agent.infrastructure.persistence.mapper.ToolRegistryVersionMapper;
import com.example.agent.infrastructure.persistence.po.ToolRegistryVersionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工具版本历史仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ToolRegistryVersionRepositoryImpl implements ToolRegistryVersionRepository {

    private final ToolRegistryVersionMapper mapper;

    @Override
    public void save(ToolRegistryVersion version) {
        mapper.insert(toPO(version));
    }

    @Override
    public Optional<ToolRegistryVersion> findByToolIdAndVersion(String toolId, int version) {
        return mapper.selectByToolIdAndVersion(toolId, version).map(this::toDomain);
    }

    @Override
    public List<ToolRegistryVersion> findByToolId(String toolId) {
        return mapper.selectByToolId(toolId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Integer> findMaxVersionByToolId(String toolId) {
        return mapper.selectMaxVersionByToolId(toolId);
    }

    @Override
    public int countByToolId(String toolId) {
        return mapper.countByToolId(toolId);
    }

    // ==================== 映射方法 ====================

    private ToolRegistryVersion toDomain(ToolRegistryVersionPO po) {
        return ToolRegistryVersion.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .toolId(po.getToolId())
                .version(po.getVersion())
                .toolName(po.getToolName())
                .toolType(ToolType.fromCode(po.getToolType()))
                .endpointUrl(po.getEndpointUrl())
                .inputSchema(po.getInputSchema())
                .outputSchema(po.getOutputSchema())
                .authConfigJson(po.getAuthConfigJson())
                .requireApproval(po.getRequireApproval())
                .description(po.getDescription())
                .changeReason(po.getChangeReason())
                .createdAt(po.getCreatedAt())
                .build();
    }

    private ToolRegistryVersionPO toPO(ToolRegistryVersion version) {
        return ToolRegistryVersionPO.builder()
                .tenantId(version.getTenantId())
                .toolId(version.getToolId())
                .version(version.getVersion())
                .toolName(version.getToolName())
                .toolType(version.getToolType().name())
                .endpointUrl(version.getEndpointUrl())
                .inputSchema(version.getInputSchema())
                .outputSchema(version.getOutputSchema())
                .authConfigJson(version.getAuthConfigJson())
                .requireApproval(version.getRequireApproval())
                .description(version.getDescription())
                .changeReason(version.getChangeReason())
                .createdAt(version.getCreatedAt())
                .build();
    }
}
