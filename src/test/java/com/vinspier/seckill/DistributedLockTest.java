package com.vinspier.seckill;


import com.vinspier.seckill.enums.DistributedLockKey;
import com.vinspier.seckill.lock.RedissionDistributedLockService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecKillApplication.class})
public class DistributedLockTest {

    private final Logger logger = LoggerFactory.getLogger(DistributedLockTest.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissionDistributedLockService redissionDistributedLockService;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static final String TEST_COUNT_KEY = "TEST_COUNT_KEY";
    private static final int TEST_INIT_COUNT = 1000000;

    /**
     * 设定初始值
     * */
    @Test
    public void initTestCount(){
        redisTemplate.opsForValue().set(TEST_COUNT_KEY,TEST_INIT_COUNT);
    }

    /**
     * 高并发请求 减数
     * 无分布式锁
     * */
    @Test
    public void parallelDecrementWithoutLock(){
        int times = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(times);
        Integer originCount = (Integer) redisTemplate.opsForValue().get(TEST_COUNT_KEY);
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++){
            executorService.execute(() -> {
                countDownLatch.countDown();
                // 模拟并发场景 非原子性 操作
                Integer originCount1 = (Integer) redisTemplate.opsForValue().get(TEST_COUNT_KEY);
                redisTemplate.opsForValue().set(TEST_COUNT_KEY,originCount1 - 1);
            });
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        Integer newCount = (Integer) redisTemplate.opsForValue().get(TEST_COUNT_KEY);
        logger.info("prepare decrease count={} ,originCount={},newCount={},time took={}ms",times,originCount,newCount,end - start);
        executorService.shutdown();
    }

    /**
     * 高并发请求 减数
     * 无分布式锁
     * */
    @Test
    public void parallelDecrementWithRedissionLock(){
        int times = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(times);
        Integer originCount = (Integer) redisTemplate.opsForValue().get(TEST_COUNT_KEY);
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++){
            executorService.execute(() -> {
                countDownLatch.countDown();
                boolean locked = redissionDistributedLockService.lock(DistributedLockKey.REDISSION_KEY.toString());
                if (locked){
                    redisTemplate.opsForValue().decrement(TEST_COUNT_KEY);
                    redissionDistributedLockService.unlock(DistributedLockKey.REDISSION_KEY.toString());
                }
            });
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        Integer newCount = (Integer) redisTemplate.opsForValue().get(TEST_COUNT_KEY);
        logger.info("[----------info]prepare decrease count={} ,originCount={},newCount={},time took={}ms",times,originCount,newCount,end - start);
        executorService.shutdown();
    }

}
