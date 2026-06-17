package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.KnowledgeBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 知识库 MyBatis Mapper.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Mapper
public interface KnowledgeBaseMapper {

    int insert(KnowledgeBasePO po);

    int update(KnowledgeBasePO po);

    int updateDocumentCount(@Param("knowledgeId") String knowledgeId, @Param("documentCount") int documentCount);

    int incrementDocumentCount(@Param("knowledgeId") String knowledgeId);

    int decrementDocumentCount(@Param("knowledgeId") String knowledgeId);

    int updateChunkStrategy(@Param("knowledgeId") String knowledgeId,
                            @Param("defaultChunkStrategy") String defaultChunkStrategy,
                            @Param("chunkConfigJson") String chunkConfigJson);

    int updateIndexConfig(@Param("knowledgeId") String knowledgeId,
                          @Param("indexType") String indexType,
                          @Param("indexParamsJson") String indexParamsJson);

    int updateSearchConfig(@Param("knowledgeId") String knowledgeId,
                           @Param("searchStrategy") String searchStrategy,
                           @Param("searchParamsJson") String searchParamsJson,
                           @Param("multiStageParamsJson") String multiStageParamsJson,
                           @Param("monitoringParamsJson") String monitoringParamsJson);

    int updateStatus(@Param("knowledgeId") String knowledgeId, @Param("status") String status);

    Set<String> findEnabledKnowledgeIds(@Param("tenantId") Long tenantId);

    List<KnowledgeBasePO> selectByTenantAndStatus(@Param("tenantId") Long tenantId, @Param("status") String status);

    int softDelete(@Param("knowledgeId") String knowledgeId);

    Optional<KnowledgeBasePO> selectByKnowledgeId(@Param("knowledgeId") String knowledgeId);

    List<KnowledgeBasePO> selectByTenant(@Param("tenantId") Long tenantId,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    long countByTenant(@Param("tenantId") Long tenantId);
}
