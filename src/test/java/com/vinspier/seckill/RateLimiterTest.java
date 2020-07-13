package com.vinspier.seckill;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class RateLimiterTest {

    private static final Logger logger = LoggerFactory.getLogger(SecKillApplicationTest.class);
    private static final CountDownLatch countDownLatch = new CountDownLatch(400);

    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(5);
        for (int i = 0; i < 400; i++){
            new Thread(() -> {
                countDownLatch.countDown();
                if (rateLimiter.tryAcquire(500, TimeUnit.MILLISECONDS)){
                    logger.info("Task done well @[{}]",Thread.currentThread().getName() );
                }else {
                   // logger.info("Task execute failed @[{}]",Thread.currentThread().getName() );
                }
            }).start();
        }
    }
}
