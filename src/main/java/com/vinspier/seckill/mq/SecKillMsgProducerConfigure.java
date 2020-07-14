package com.vinspier.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PrefixKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 秒杀消息生产者的回调配置类
 *
 * 监听 消息是否正确到达交换机或者匹配到队列
 * */
@Component
public class SecKillMsgProducerConfigure implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback{

    private final Logger logger = LoggerFactory.getLogger(SecKillMsgProducerConfigure.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);            //指定 ConfirmCallback
        rabbitTemplate.setReturnCallback(this);             //指定 ReturnCallback

    }

    /**
     * 确认消息是否到达exchange
     * @Param b: true 到达 false 未到达
     * */
    @Override
    public void confirm(@Nullable CorrelationData correlationData, boolean b, @Nullable String s) {
        // 若没有成功发送消息到 交换机
        // 认为该用户秒杀失败 删除redis中的预购信息
        Message message = correlationData.getReturnedMessage();
        if (!b && message != null && message.getBody().length > 0){
            String body = new String(correlationData.getReturnedMessage().getBody());
            SecKillMsg secKillMsg = JSONObject.parseObject(body,SecKillMsg.class);
            logger.info("secKillMsg didn't send success to exchange and remove preGrab=[{}@{}] from redis prefixKey=[{}] ",secKillMsg.getSecKillId(),secKillMsg.getUserPhone(),PrefixKey.SEC_KILLED_PRE_GRABS);
            // redis库存 加回1
            redisTemplate.opsForValue().increment(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + secKillMsg.getSecKillId());
            // 删除redis中 排队中的用户
            redisTemplate.opsForSet().remove(PrefixKey.SEC_KILLED_PRE_GRABS.getPrefix(),secKillMsg.getSecKillId() + "@" + secKillMsg.getUserPhone());
        }
    }

    /**
     * 确认消息没有正确到达队列
     * 没有 则回调这个方法
     * */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {

    }
}
