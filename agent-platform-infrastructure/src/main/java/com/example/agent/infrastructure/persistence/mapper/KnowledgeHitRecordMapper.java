package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.KnowledgeHitRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识命中记录 MyBatis Mapper.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Mapper
public interface KnowledgeHitRecordMapper {

    int insert(KnowledgeHitRecordPO po);

    int batchInsert(@Param("records") List<KnowledgeHitRecordPO> records);

    List<KnowledgeHitRecordPO> selectByConversationId(@Param("conversationId") String conversationId,
                                                       @Param("offset") int offset,
                                                       @Param("size") int size);

    long countByConversationId(@Param("conversationId") String conversationId);

    List<KnowledgeHitRecordPO> selectByChunkId(@Param("chunkId") Long chunkId,
                                                @Param("offset") int offset,
                                                @Param("size") int size);

    int updateFeedback(@Param("id") Long id,
                       @Param("humanFeedback") String humanFeedback,
                       @Param("feedbackNote") String feedbackNote);

    List<KnowledgeHitRecordPO> selectPositiveFeedbackByTenant(@Param("tenantId") String tenantId,
                                                               @Param("limit") int limit);

    long countByTenantAndDateRange(@Param("tenantId") String tenantId,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate);
}
