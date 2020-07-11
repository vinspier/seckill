package com.vinspier.seckill.mq;

public interface RabbitKeys {
    /** 交换机名称 */
    String EXCHANGE_NAME = "sec_kill";
    /** 秒杀成功获得机会的队列 */
    String GRAB_CHANCE_QUEUE = "grab_chance_queue";
    /** 秒杀成功获得机会的消费路由key */
    String GRAB_KEY = "grab";
}
