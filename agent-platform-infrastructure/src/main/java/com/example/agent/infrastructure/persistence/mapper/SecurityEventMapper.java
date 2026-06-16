package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.SecurityEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 安全事件 MyBatis Mapper — 对应 t_security_event 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface SecurityEventMapper {

    int insert(SecurityEventPO po);

    List<SecurityEventPO> selectByTenant(@Param("tenantId") String tenantId,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    long countByTenant(@Param("tenantId") String tenantId);

    List<SecurityEventPO> selectByConversation(@Param("conversationId") String conversationId,
                                                @Param("offset") int offset,
                                                @Param("size") int size);

    List<SecurityEventPO> selectByEventType(@Param("tenantId") String tenantId,
                                             @Param("eventType") String eventType,
                                             @Param("offset") int offset,
                                             @Param("size") int size);
}
