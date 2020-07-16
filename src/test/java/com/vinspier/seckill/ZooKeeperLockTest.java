package com.vinspier.seckill;

import com.vinspier.seckill.config.ZooKeeperProperties;
import com.vinspier.seckill.lock.ZooKeeperDistributedLockService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecKillApplication.class})
public class ZooKeeperLockTest {

    private final Logger logger = LoggerFactory.getLogger(ZooKeeperLockTest.class);

    @Autowired
    private ZooKeeperDistributedLockService zooKeeperDistributedLockService;
    @Autowired
    private ZooKeeperProperties zooKeeperProperties;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static final String LOCK_PATH = "/LOCK_DISTRIBUTED";
    private static final int TEST_INIT_COUNT = 600;

    /** 计数器 模拟共享变量 */
    public static long countNumber = 0;

    @Test
    public void addWithoutLock(){
        long originCount = countNumber;
        CountDownLatch countDownLatch = new CountDownLatch(TEST_INIT_COUNT);
        for (int i = 0; i < TEST_INIT_COUNT; i++){
            executorService.execute(() -> {
                countDownLatch.countDown();
                countNumber ++;
            });
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long newCount = countNumber;
        executorService.shutdown();
        logger.info("[>>>>>>>>info:prepare decrease count={} ,originCount={},newCount={},time took={}ms]",TEST_INIT_COUNT,originCount,newCount);
    }

    @Test
    public void addWithLock(){
        long originCount = countNumber;
        String path = zooKeeperProperties.getLockRoot() + LOCK_PATH;
        CountDownLatch countDownLatch = new CountDownLatch(TEST_INIT_COUNT);
        for (int i = 0; i < TEST_INIT_COUNT; i++){
            executorService.execute(() -> {
                countDownLatch.countDown();
                zooKeeperDistributedLockService.acquireLock(path,2000, TimeUnit.MILLISECONDS,()->{
                    countNumber ++;
                });
            });
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long newCount = countNumber;
        executorService.shutdown();
        logger.info("[>>>>>>>>info:prepare decrease count={} ,originCount={},newCount={},time took={}ms]",TEST_INIT_COUNT,originCount,newCount);
    }

}
