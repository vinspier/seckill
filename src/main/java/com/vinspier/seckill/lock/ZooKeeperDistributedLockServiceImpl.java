package com.vinspier.seckill.lock;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZooKeeperDistributedLockServiceImpl implements ZooKeeperDistributedLockService{

    @Autowired
    private CuratorFramework curatorFramework;

    /**
     * 该方法为模板方法，获得锁后回调 BaseLockHandler 中的 handler 方法
     * @return
     */
    public <T> T acquireLock(BaseLockHandler<T> baseLockHandler) {
        return null;
    }

}
