package com.vinspier.seckill.lock;

public interface ZooKeeperDistributedLockService {

    <T> T acquireLock(BaseLockHandler<T> baseLockHandler);

}
