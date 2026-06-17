package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tenant.User;
import com.example.agent.domain.tenant.UserRepository;
import com.example.agent.infrastructure.persistence.mapper.UserMapper;
import com.example.agent.infrastructure.persistence.po.UserPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储 MyBatis 数据库实现
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        return userMapper.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return userMapper.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Optional<User> findByTenantAndUsername(Long tenantId, String username) {
        return userMapper.findByTenantAndUsername(tenantId, username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username).map(this::toDomain);
    }

    @Override
    public List<User> findByTenant(Long tenantId, int page, int size) {
        return userMapper.findByTenant(tenantId, page * size, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByTenant(Long tenantId) {
        return userMapper.countByTenant(tenantId);
    }

    @Override
    public void save(User user) {
        userMapper.insert(toPO(user));
    }

    @Override
    public void update(User user) {
        userMapper.update(toPO(user));
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    // ==================== 映射方法 ====================

    private User toDomain(UserPO po) {
        return User.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .userId(po.getUserId())
                .username(po.getUsername())
                .passwordHash(po.getPasswordHash())
                .email(po.getEmail())
                .phone(po.getPhone())
                .status(po.getStatus())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .deleted(po.getDeleted())
                .build();
    }

    private UserPO toPO(User user) {
        return UserPO.builder()
                .id(user.getId())
                .tenantId(user.getTenantId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .passwordHash(user.getPasswordHash())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deleted(user.getDeleted())
                .build();
    }
}
