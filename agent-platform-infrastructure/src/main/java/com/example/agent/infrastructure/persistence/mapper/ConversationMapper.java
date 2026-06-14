package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.ConversationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 会话 MyBatis Mapper — 单表查询，禁止 JOIN.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface ConversationMapper {

    int insert(ConversationPO po);

    Optional<ConversationPO> selectByConversationId(@Param("conversationId") String conversationId);

    List<ConversationPO> selectByUserId(@Param("tenantId") String tenantId,
                                         @Param("userId") String userId,
                                         @Param("offset") int offset,
                                         @Param("size") int size);

    long countByUserId(@Param("tenantId") String tenantId,
                       @Param("userId") String userId);

    int updateTitle(@Param("conversationId") String conversationId,
                    @Param("title") String title);

    int updateStatus(@Param("conversationId") String conversationId,
                     @Param("status") String status);

    int incrementMessageCount(@Param("conversationId") String conversationId,
                              @Param("delta") int delta);

    int addTokens(@Param("conversationId") String conversationId,
                  @Param("tokens") long tokens);

    int softDelete(@Param("conversationId") String conversationId);
}
