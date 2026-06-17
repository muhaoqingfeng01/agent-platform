package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 用户 MyBatis Mapper
 */
@Mapper
public interface UserMapper {

    Optional<UserPO> findById(@Param("id") Long id);

    Optional<UserPO> findByUserId(@Param("userId") String userId);

    Optional<UserPO> findByTenantAndUsername(@Param("tenantId") Long tenantId,
                                              @Param("username") String username);

    Optional<UserPO> findByUsername(@Param("username") String username);

    List<UserPO> findByTenant(@Param("tenantId") Long tenantId,
                               @Param("offset") int offset,
                               @Param("size") int size);

    long countByTenant(@Param("tenantId") Long tenantId);

    int insert(UserPO user);

    int update(UserPO user);

    int deleteById(@Param("id") Long id);
}
