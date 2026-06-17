package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.AuditLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * t_audit_log MyBatis Mapper 接口.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface AuditLogMapper {

    int insert(AuditLogPO po);

    List<AuditLogPO> selectByTraceId(@Param("traceId") String traceId);

    List<AuditLogPO> selectByTenant(@Param("tenantId") Long tenantId,
                                    @Param("offset") int offset,
                                    @Param("size") int size);

    List<AuditLogPO> selectByConversation(@Param("conversationId") String conversationId,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    long countByTenant(@Param("tenantId") Long tenantId);
}
