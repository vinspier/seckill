package com.vinspier.seckill;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PrefixKeyEnum;
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
            seckillRedisTemplate.opsForValue().set(PrefixKeyEnum.SEC_KILLED_GOODS.getPrefix() + secKill.getSeckillId(),secKill);
        }
    }

    @Test
    public void getFormRedis(){
        SecKill secKill = seckillRedisTemplate.opsForValue().get(PrefixKeyEnum.SEC_KILLED_GOODS.getPrefix() + 1000L);
        System.out.println(secKill.toString());
    }

}