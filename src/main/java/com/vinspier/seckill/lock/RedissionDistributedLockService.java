package com.vinspier.seckill.lock;

public interface RedissionDistributedLockService {

    /** 获取分布式锁 */
    Boolean lock(String lockName);
    /** 释放分布式锁 */
    Boolean unlock(String lockName);

}
