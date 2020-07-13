package com.vinspier.seckill;

import com.google.common.util.concurrent.RateLimiter;
import com.vinspier.seckill.config.CustomizeProperties;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PayOrderState;
import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.service.PayOrderService;
import com.vinspier.seckill.service.SecKillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecKillApplication.class})
public class SecKillApplicationTest {

    private final Logger logger = LoggerFactory.getLogger(SecKillApplicationTest.class);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private SecKillService secKillService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private RedisTemplate<String,SecKill> seckillRedisTemplate;
    @Autowired
    private CustomizeProperties customizeProperties;

    @Test
    public void findById(){
        System.out.println(secKillService.findById(1000L).toString());
    }

    @Test
    public void initRedis(){
        SecKill secKill = secKillService.findById(1000L);
        if (secKill != null){
            seckillRedisTemplate.opsForValue().set(PrefixKey.SEC_KILLED_GOODS.getPrefix() + secKill.getSeckillId(),secKill);
        }
    }

    @Test
    public void getFormRedis(){
        SecKill secKill = seckillRedisTemplate.opsForValue().get(PrefixKey.SEC_KILLED_GOODS.getPrefix() + 1000L);
        System.out.println(secKill.toString());
    }

    @Test
    public void exposed(){
        logger.info("secKill exposed md5 = {}",secKillService.exposed(1000L).getMd5());
    }

    @Test
    public void multiGrab(){
        String md5 = "f6e12a983a64dd3365f966786a6d5b76";
        long id = 1000;
        new Thread(() -> {
            grab(id,10086,md5);
        });
    }

    public void grab(long id,long phone,String md5){
        secKillService.grab(id,phone,md5);
    }

    /**
     * 测试google guava rateLimiter
     * */
    @Test
    public void rateLimiter(){
        RateLimiter rateLimiter = RateLimiter.create(10);
        CountDownLatch countDownLatch = new CountDownLatch(500);
        for (int i = 0; i < 500; i++){
            new Thread(() -> {
                countDownLatch.countDown();
                if (rateLimiter.tryAcquire(1000,TimeUnit.MILLISECONDS)){
                    logger.info("Task done well @[{}]",Thread.currentThread().getName() );
                }else {
                    logger.info("Task execute failed @[{}]",Thread.currentThread().getName() );
                }
            }).start();
        }
    }

    /**
     * 模拟抢购接口
     * cyclicBarrier 当成是nginx的限流
     * */
    @Test
    public void simulationGrab(){
        CountDownLatch countDownLatch = new CountDownLatch(50);
        for (long i = 0; i < 50; i++){
            long index = 177000000 + i;
            executorService.execute(() -> {
                try {
                    logger.info("index={}",index);
                    countDownLatch.countDown();
                    secKillService.grab(1000L,index,"f6e12a983a64dd3365f966786a6d5b76");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        try {
            // 模拟服务器运行中 等待mq 消息处理玩
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    /**
     * 测试更新 超时未支付订单状态
     * */
    @Test
    public void updateOrderExpiredState(){
        payOrderService.payExpiredStateSet(PayOrderState.GRAB_SUCCEUSS.getState(),PayOrderState.INVALID.getState(),customizeProperties.getPayedWaited());
    }
}