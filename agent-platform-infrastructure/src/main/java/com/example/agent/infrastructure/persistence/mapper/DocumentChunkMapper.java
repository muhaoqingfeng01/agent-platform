package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.DocumentChunkPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档切片 MyBatis Mapper.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Mapper
public interface DocumentChunkMapper {

    int insert(DocumentChunkPO po);

    int batchInsert(@Param("chunks") List<DocumentChunkPO> chunks);

    List<DocumentChunkPO> selectByDocumentId(@Param("documentId") String documentId);

    List<DocumentChunkPO> selectByDocumentIdPaged(@Param("documentId") String documentId,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);

    List<DocumentChunkPO> selectByIds(@Param("ids") List<Long> ids);

    int countByDocumentId(@Param("documentId") String documentId);

    int deleteByDocumentId(@Param("documentId") String documentId);

    int deleteByKnowledgeId(@Param("knowledgeId") String knowledgeId);

    int softDeleteByDocumentId(@Param("documentId") String documentId);

    int softDeleteByKnowledgeId(@Param("knowledgeId") String knowledgeId);
}
