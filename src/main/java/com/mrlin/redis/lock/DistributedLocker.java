package com.mrlin.redis.lock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @description: 锁基类
 * @date: 2020/5/28 0028 14:48
 * @author: xpy
 */
public interface DistributedLocker {
    RLock lock(String lockKey);

    RLock lock(String lockKey, long timeout);

    RLock lock(String lockKey, TimeUnit unit, long timeout);

    boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime);

    boolean tryLock(String lockKey, long waitTime, long leaseTime);

    boolean tryLock(String lockKey, long leaseTime);

    void unlock(String lockKey);

    void unlock(RLock lock);

    RLock getRLock(String lockKey);
}
