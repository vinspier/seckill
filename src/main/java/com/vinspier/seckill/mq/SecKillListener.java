package com.vinspier.seckill.mq;


import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 监听秒杀活动的后续流程
 * */
@Component
public class SecKillListener {

    private final Logger logger = LoggerFactory.getLogger(SecKillListener.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SecKillService secKillService;

    /**
     * 成功抢到机会
     * 在redis中处理库存问题
     * */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitKeys.GRAB_CHANCE_QUEUE),
            exchange = @Exchange(value = RabbitKeys.EXCHANGE_NAME,ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {RabbitKeys.GRAB_KEY}
        )
    )
    public void grabListener(SecKillMsg secKillMsg){
        logger.info("user={} got an chance to buy secKill={} ",secKillMsg.getUserPhone(),secKillMsg.getSecKillId());
        secKillService.doGrabInRedis(secKillMsg);
        // ToDo 发送ack确认消息被正确接受消费
    }

}
