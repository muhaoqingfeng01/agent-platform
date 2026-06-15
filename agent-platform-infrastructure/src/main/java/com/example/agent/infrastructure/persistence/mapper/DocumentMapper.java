package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.DocumentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 文档 MyBatis Mapper.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Mapper
public interface DocumentMapper {

    int insert(DocumentPO po);

    int update(DocumentPO po);

    int updateStatus(@Param("documentId") String documentId, @Param("status") String status);

    int updateChunkCount(@Param("documentId") String documentId, @Param("chunkCount") int chunkCount);

    int updateErrorMessage(@Param("documentId") String documentId, @Param("errorMessage") String errorMessage);

    int softDelete(@Param("documentId") String documentId);

    int softDeleteByKnowledgeId(@Param("knowledgeId") String knowledgeId);

    long countByKnowledgeIdAndStatus(@Param("knowledgeId") String knowledgeId,
                                     @Param("status") String status);

    Optional<DocumentPO> selectByDocumentId(@Param("documentId") String documentId);

    List<DocumentPO> selectByKnowledgeId(@Param("knowledgeId") String knowledgeId,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    long countByKnowledgeId(@Param("knowledgeId") String knowledgeId);

    List<DocumentPO> selectByStatus(@Param("status") String status);

    /** ★ 新增: 批量按 ID 查询，用于检索结果溯源 */
    List<DocumentPO> selectByDocumentIds(@Param("documentIds") java.util.Set<String> documentIds);
}
