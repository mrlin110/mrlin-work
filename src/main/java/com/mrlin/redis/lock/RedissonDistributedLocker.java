package com.mrlin.redis.lock;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Redisson的分布式锁实现
 * @Author: ljm
 * @Date: 2020/12/7 15:11
 * @Version: 1.0
 */
@Component
public class RedissonDistributedLocker implements DistributedLocker {
    private final String prefix = "RLock:";

    @Resource
    private RedissonClient redissonClient;
    
    /**
     * @description: 获取锁如果当前资源已被锁上则当前线程挂起，直到其他线程释放资源
     * @date: 2020/12/7 0011 15:26
     * @param lockKey 需要锁的资源
     * @author: ljm
     */
    @Override
    public RLock lock(String lockKey) {
        RLock lock = this.getRLock(prefix+lockKey);
        lock.lock();
        return lock;
    }
    /**
     * @description: 获取锁如果当前资源已被锁上则当前线程挂起，直到其他线程释放资源
     * @date: 2020/12/07 0016 15:26
     * @param lockKey 需要锁的资源
     * @param lockTime 加锁时间（毫秒）
     * @author: ljm
     */
    @Override
    public RLock lock(String lockKey, long lockTime) {
        RLock lock = this.getRLock(prefix+lockKey);
        lock.lock(lockTime, TimeUnit.MILLISECONDS);
        return lock;
    }
    /**
     * @description: 获取锁如果当前资源已被锁上则当前线程挂起，直到其他线程释放资源
     * @date: 2020/12/07 0016 15:26
     * @param lockKey 需要锁的资源
     * @param unit 加锁时间单位
     * @param lockTime 加锁时间
     * @author: ljm
     */
    @Override
    public RLock lock(String lockKey, TimeUnit unit, long lockTime) {
        RLock lock = this.getRLock(prefix+lockKey);
        lock.lock(lockTime, unit);
        return lock;
    }
    /**
     * @description: 尝试对资源上锁 获取成功返回true 否则返回 false
     *               在尝试时间内线程会等待直至时间超时或其他线程释放资源
     *               当尝试时间到还未获取到资源锁则返回false
     *
     * @date: 2020/12/07 0016 15:26
     * @param lockKey 需要锁的资源
     * @param unit 加锁时间单位
     * @param waitTime 尝试获取锁最长等待时长
     * @param lockTime 加锁时间
     * @author: ljm
     */
    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long lockTime) {
        RLock lock = this.getRLock(prefix+lockKey);
        try {
            return lock.tryLock(waitTime, lockTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * @description: 尝试对资源上锁 获取成功返回true 否则返回 false
     *               在尝试时间内线程会等待直至时间超时或其他线程释放资源
     *               当尝试时间到还未获取到资源锁则返回false
     * @date: 2020/12/07 0016 15:26
     * @param lockKey 需要锁的资源
     * @param waitTime 尝试获取锁最长等待时长（毫秒）
     * @param leaseTime 加锁时间(毫秒)
     * @author: ljm
     */
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        return this.tryLock(prefix+lockKey,TimeUnit.MILLISECONDS,waitTime,leaseTime);
    }

    /**
     * @description: 尝试对资源上锁 获取成功返回true 否则返回 false
     *               在尝试时间内线程会等待直至时间超时或其他线程释放资源
     *               当尝试时间到还未获取到资源锁则返回false
     * @date: 2020/12/07 0016 15:26
     * @param lockKey 需要锁的资源
     * @param leaseTime 加锁时间(毫秒)
     * @author: ljm
     */
    @Override
    public boolean tryLock(String lockKey, long leaseTime) {
        return this.tryLock(prefix+lockKey,TimeUnit.MILLISECONDS,0,leaseTime);
    }

    /**
     * @description: 解锁
     * @date: 2020/12/07 0016 15:39
     * @author: ljm
     * @param lockKey 需要解锁资源
     */
    @Override
    public void unlock(String lockKey) {
        RLock lock = this.getRLock(prefix+lockKey);
        if(lock.isLocked()){
            lock.unlock();
        }
    }
    /**
     * @description: 解锁
     * @date: 2020/12/07 0016 15:39
     * @param lock 锁对象
     */
    @Override
    public void unlock(RLock lock) {
        if(lock.isLocked()){
            lock.unlock();
        }
    }

    @Override
    public RLock getRLock(String lockKey){
        return redissonClient.getLock(prefix+lockKey);
    }



}
