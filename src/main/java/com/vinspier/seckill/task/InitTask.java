package com.vinspier.seckill.task;

import com.vinspier.seckill.dao.SecKillDao;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PrefixKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 程序启动后的任务
 * 将未添加到redis中的seckill商品信息存入redis中
 * */

@Component
@Order(value = 1)
public class InitTask implements CommandLineRunner{

    private final Logger logger = LoggerFactory.getLogger(InitTask.class);

    @Autowired
    private SecKillDao secKillDao;
    @Autowired
    private RedisTemplate<String,SecKill> seckillRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 预热秒杀数据到Redis
     * */
    @Override
    public void run(String... args) throws Exception {
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<程序启动后，执行初始化任务>>>>>>>>>>>>>>>>>>>>>>>>");
//        RowBounds rowBounds = new RowBounds(0,10);
//        List<SecKill> secKills = secKillDao.selectByRowBounds(new SecKill(),rowBounds);
        List<SecKill> secKills = secKillDao.selectAll();
        if (!CollectionUtils.isEmpty(secKills)){
            secKills.forEach(s -> {
                // 如果未存在 则存入redis中
                seckillRedisTemplate.opsForValue().setIfAbsent(PrefixKey.SEC_KILLED_GOODS.getPrefix() + s.getSeckillId().toString(),s);
                redisTemplate.opsForValue().setIfAbsent(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + s.getSeckillId().toString(),s.getInventory());
                if (!redisTemplate.opsForSet().isMember(PrefixKey.SEC_KILLED_IDS.getPrefix(),s.getSeckillId())){
                    redisTemplate.opsForSet().add(PrefixKey.SEC_KILLED_IDS.getPrefix(),s.getSeckillId());
                }
            });
        }
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<程序启动后，完成初始化任务>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
