package com.vinspier.seckill.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedissionDistributedLockServiceImpl implements RedissionDistributedLockService {

    private final Logger logger = LoggerFactory.getLogger(RedissionDistributedLockServiceImpl.class);

    @Resource
    private RedissonClient redissonClient;

    // 加锁
    public Boolean lock(String lockName) {
        try {
            if (redissonClient == null) {
                logger.info("DistributedRedisLock redissonClient is null");
                return false;
            }

            RLock lock = redissonClient.getLock(lockName);
            // 锁5秒后自动释放，防止死锁
            lock.lock(5, TimeUnit.SECONDS);

            logger.info("Thread [{}] DistributedRedisLock lock [{}] success", Thread.currentThread().getName(), lockName);
            // 加锁成功
            return true;
        } catch (Exception e) {
            logger.error("DistributedRedisLock lock [{}] Exception:", lockName, e);
            return false;
        }
    }

    // 释放锁
    public Boolean unlock(String lockName) {
        try {
            if (redissonClient == null) {
                logger.info("DistributedRedisLock redissonClient is null");
                return false;
            }

            RLock lock = redissonClient.getLock(lockName);
            lock.unlock();
            logger.info("Thread [{}] DistributedRedisLock unlock [{}] success", Thread.currentThread().getName(), lockName);
            // 释放锁成功
            return true;
        } catch (Exception e) {
            logger.error("DistributedRedisLock unlock [{}] Exception:", lockName, e);
            return false;
        }
    }
}
