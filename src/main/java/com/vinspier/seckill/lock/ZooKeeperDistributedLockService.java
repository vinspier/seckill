package com.vinspier.seckill.lock;

import java.util.concurrent.TimeUnit;

/**
* @description: 使用curator操作zookeeper
* @author: vinspeir
* @date:2020/7/16 11:37
*/
public interface ZooKeeperDistributedLockService {

    void acquireLock(String path,LockAcquiredCallback acquiredCallback);
    void acquireLock(String path, int timeOut, TimeUnit timeUnit, LockAcquiredCallback acquiredCallback);

}
