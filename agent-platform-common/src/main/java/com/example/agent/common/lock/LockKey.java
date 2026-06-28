package com.example.agent.common.lock;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class LockKey {

    private String catTag;
    /**
     * 锁key
     */
    private String lockName;
}
