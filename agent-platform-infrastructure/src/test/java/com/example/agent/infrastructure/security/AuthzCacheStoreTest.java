package com.example.agent.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuthzCacheStore 单元测试.
 */
@ExtendWith(MockitoExtension.class)
class AuthzCacheStoreTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private SetOperations<String, String> setOperations;

    private AuthzCacheStore store;

    @BeforeEach
    void setUp() {
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        store = new AuthzCacheStore(stringRedisTemplate, new ObjectMapper());
    }

    @Test
    void putAndGetPermissions_roundTrip() throws Exception {
        when(valueOperations.get("authz:perm:u1"))
                .thenReturn("[\"conversation:read\",\"kb:read\"]");

        Optional<List<String>> cached = store.getPermissions("u1");
        assertTrue(cached.isPresent());
        assertEquals(List.of("conversation:read", "kb:read"), cached.get());

        store.putPermissions("u1", List.of("a:b"));
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq("authz:perm:u1"), jsonCaptor.capture(), any(Duration.class));
        assertTrue(jsonCaptor.getValue().contains("a:b"));
    }

    @Test
    void markOnlineAndList() {
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members("authz:online")).thenReturn(Set.of("u1", "u2"));

        store.markOnline("u1");
        verify(setOperations).add("authz:online", "u1");

        assertEquals(Set.of("u1", "u2"), store.listOnlineUserIds());
    }

    @Test
    void clearUser_removesKeys() {
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);

        store.clearUser("u1");
        verify(setOperations).remove("authz:online", "u1");
        verify(stringRedisTemplate).delete("authz:perm:u1");
        verify(stringRedisTemplate).delete("authz:role:u1");
    }
}
