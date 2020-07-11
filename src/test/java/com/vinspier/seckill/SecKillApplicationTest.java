package com.vinspier.seckill;

import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.service.SecKillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecKillApplicationTest {

    private final Logger logger = LoggerFactory.getLogger(SecKillApplicationTest.class);

    @Autowired
    private SecKillService secKillService;
    @Autowired
    private RedisTemplate<String,SecKill> seckillRedisTemplate;

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

    @Test
    public void grab(long id,long phone,String md5){
        secKillService.grab(id,phone,md5);
    }
}