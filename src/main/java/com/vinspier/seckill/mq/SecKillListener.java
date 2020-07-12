package com.vinspier.seckill.mq;


import com.rabbitmq.client.Channel;
import com.vinspier.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
    public void grabListener(SecKillMsg secKillMsg, Message message, Channel channel) throws IOException{
        try {
            secKillService.doGrabInRedis(secKillMsg);
            // 发送ack确认消息被正确接受消费
            // false只确认当前consumer一个消息收到，
            // true确认所有consumer获得的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
            logger.info("user={} got an chance to buy secKill={} ",secKillMsg.getUserPhone(),secKillMsg.getSecKillId());
        } catch (Exception e) {
            logger.info(e.getMessage());
            if (message.getMessageProperties().getRedelivered()) {
                logger.info("消息已重复处理失败,拒绝再次接收！");
                // 拒绝消息，requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列
                // 若 requeue=false 可能会发生死循环
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                logger.info("消息即将再次返回队列处理！");
                // requeue为是否重新回到队列，true重新入队
                // 第一个boolean表示一个consumer还是所有，
                // 第二个boolean表示requeue是否重新回到队列，true重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }

    }

}
