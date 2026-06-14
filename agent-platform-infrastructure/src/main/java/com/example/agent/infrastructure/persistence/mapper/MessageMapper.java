package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.MessagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息 MyBatis Mapper — 单表查询，禁止 JOIN.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface MessageMapper {

    int insert(MessagePO po);

    List<MessagePO> selectByConversationId(@Param("conversationId") String conversationId,
                                            @Param("offset") int offset,
                                            @Param("size") int size);

    List<MessagePO> selectBefore(@Param("conversationId") String conversationId,
                                  @Param("beforeMessageId") String beforeMessageId,
                                  @Param("size") int size);

    int updateFeedback(@Param("messageId") String messageId,
                       @Param("feedback") String feedback);
}
