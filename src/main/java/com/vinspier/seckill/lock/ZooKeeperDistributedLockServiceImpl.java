package com.vinspier.seckill.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class ZooKeeperDistributedLockServiceImpl implements ZooKeeperDistributedLockService{

    private final Logger logger = LoggerFactory.getLogger(ZooKeeperDistributedLockServiceImpl.class);

    @Resource
    private CuratorFramework curatorFramework;

    @Override
    public void acquireLock(String path, LockAcquiredCallback acquiredCallback) {
        acquireLock(path,1,TimeUnit.SECONDS,acquiredCallback);
    }

    /**
     * @description: 获取锁
     * @param: path 锁的节点路径
     * @param: timeOut 超时时间
     * @param: timeUnit 时间单位
     * @param: acquiredCallback 指定得到锁后的回调
     * @Return: void
     * @author: vinspeir
     * @date:2020/7/16 11:30
     */
    @Override
    public void acquireLock(String path, int timeOut, TimeUnit timeUnit, LockAcquiredCallback acquiredCallback) {
        //通过 InterProcessMutex 该类来获取可重入共性锁
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, path);
        boolean acquired = false;
        try {
            // acquire the lock and then do what definite in LockAcquiredCallback
            while (!acquired){
                acquired = lock.acquire(timeOut,timeUnit);
                if (acquired){
                    logger.info("[>>>>>>info: {} acquire the lock and callback ]",Thread.currentThread().getName());
                    acquiredCallback.callback();
                    // lock.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 重复获取设置的最大次数 未拿到 打断
            logger.info("[>>>>>>info: {} acquire the lock failed ]",Thread.currentThread().getName());
        }finally {
            if (acquired && lock != null && lock.isOwnedByCurrentThread()){
                try {
                    lock.release();
                    logger.info("[>>>>>>info: {} release the lock]",Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
