package com.example.agent.common.lock;

import com.example.agent.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁服务
 * 封装了 Redisson 的底层逻辑，提供统一的加锁、解锁和异常处理机制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributeLockService {

    private final RedissonClient redissonClient;


    /**
     * 尝试加锁
     * @param lockKey 锁名称
     * @param taskRunnable 业务逻辑
     * @throws Throwable
     */
    public void tryLock(LockKey lockKey, TaskRunnable taskRunnable) throws Throwable {
        String lockName = lockKey.getLockName();
        RLock lock = redissonClient.getLock(lockName);
        String threadName = Thread.currentThread().getName();
        log.info("thread:{},lockName:{},non block try qcquire lock begin", threadName, lockName);
        boolean acquire = lock.tryLock();
        validateAcquireSuccess(threadName, acquire, lockName);
        try {
            taskRunnable.run();
        } finally {
            lock.unlock();
            log.info("thread:{},lockName:{}, unlock success", threadName, lockName);
        }
    }
    /**
     * 尝试加锁
     * @param lockKey   锁的唯一标识（建议带上业务前缀，如 "order:lock:1001"）
     * @param taskCallable 业务逻辑
     * @param time
     * @param timeUnit
     */
    public <R> R tryLock(LockKey lockKey, TaskCallable<R> taskCallable,Long time,TimeUnit timeUnit) throws Throwable {
        String lockName = lockKey.getLockName();
        RLock lock = redissonClient.getLock(lockName);
        String threadName = Thread.currentThread().getName();
        boolean acquire;
        try {
            log.info("thread:{},lockName:{},non block try qcquire lock begin", threadName, lockName);
             acquire = lock.tryLock(time, timeUnit);
        } catch (InterruptedException e) {
            log.error("thread Interrupted error", e);
            Thread.currentThread().interrupt();
            throw new BusinessException("when acquire lock ，but thread Interrupted!");
        }
        validateAcquireSuccess(threadName, acquire, lockName);
        try {
            return taskCallable.call();
        } finally {
            lock.unlock();
            log.info("thread:{},lockName:{}, unlock success", threadName, lockName);
        }
    }

    /**
     * 尝试加锁
     * @param lockKey 锁的唯一标识（建议带上业务前缀，如 "order:lock:1001"）
     * @param taskCallable 业务逻辑
     * @return
     * @param
     * @throws Throwable
     */
    public <R> R tryLock(LockKey lockKey, TaskCallable<R> taskCallable) throws Throwable {

        String lockName = lockKey.getLockName();
        RLock lock = redissonClient.getLock(lockName);
        String threadName = Thread.currentThread().getName();
        log.info("thread:{},lockName:{},non block try qcquire lock begin", threadName, lockName);
        boolean  acquire = lock.tryLock();
        try {
            return taskCallable.call();
        } finally {
            lock.unlock();
            log.info("thread:{},lockName:{}, unlock success", threadName, lockName);
        }
    }












    private void validateAcquireSuccess(String threadName,boolean acquire, String lockName) {
        log.info("thread:{},lockName:{},acquire:{}", threadName, lockName, acquire);
        if (acquire) {
            return;
        }
        throw new BusinessException("when acquire lock ，but lockName: " + lockName + " is not acquired!");
    }

    /**
     * 【方式一：模板方法模式】自动获取锁并执行，执行完毕自动释放锁（推荐）
     * 适用于绝大多数常规业务场景，避免手动处理 finally 块和异常。
     *
     * @param lockKey    锁的唯一标识（建议带上业务前缀，如 "order:lock:1001"）
     * @param waitTime   尝试获取锁的最大等待时间
     * @param leaseTime  锁的持有时间（若传 null，则启用 Redisson 看门狗自动续期机制）
     * @param timeUnit   时间单位
     * @param supplier   需要加锁保护的业务逻辑
     * @param <T>        业务逻辑的返回值类型
     * @return 业务逻辑的执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, Long leaseTime,
                                 TimeUnit timeUnit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        try {
            // 1. 尝试获取锁
            isLocked = leaseTime == null
                    ? lock.tryLock(waitTime, timeUnit)
                    : lock.tryLock(waitTime, leaseTime, timeUnit);

            if (isLocked) {
                // 2. 成功获取锁，执行业务逻辑
                return supplier.get();
            } else {
                // 3. 等待超时仍未获取到锁，抛出业务异常或返回默认值
                log.warn("获取分布式锁超时，lockKey: {}", lockKey);
                throw new RuntimeException("系统繁忙，请稍后重试");
            }
        } catch (InterruptedException e) {
            // 4. 捕获中断异常，恢复中断状态并抛出异常
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断，lockKey: {}", lockKey, e);
            throw new RuntimeException("获取锁被中断");
        } finally {
            // 5. 确保在 finally 中安全释放锁，防止误删其他线程的锁
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("分布式锁已释放，lockKey: {}", lockKey);
            }
        }
    }

    /**
     * 【方式二：手动控制模式】仅获取锁，由调用方自行在 finally 中释放
     * 适用于需要在不同方法间传递锁，或复杂的跨步骤业务逻辑。
     *
     * @param lockKey   锁的唯一标识
     * @param waitTime  最大等待时间
     * @param leaseTime 锁的持有时间（传 null 启用看门狗）
     * @param timeUnit  时间单位
     * @return 锁对象（调用方需自行处理解锁）
     */
    public RLock acquireLock(String lockKey, long waitTime, Long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = leaseTime == null
                    ? lock.tryLock(waitTime, timeUnit)
                    : lock.tryLock(waitTime, leaseTime, timeUnit);
            if (isLocked) {
                return lock;
            }
            throw new RuntimeException("获取分布式锁失败，lockKey: " + lockKey);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取分布式锁被中断，lockKey: " + lockKey);
        }
    }
}