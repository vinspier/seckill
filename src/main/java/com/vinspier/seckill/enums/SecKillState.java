package com.vinspier.seckill.enums;

public enum  SecKillState {

    ENQUEUE_PRE_SECKILL(6, "排队中..."),
    /**
     * 释放分布式锁失败，秒杀被淘汰
     */
    DISTLOCK_RELEASE_FAILED(5, "很遗憾没抢到"),
    /**
     * 获取分布式锁失败，秒杀被淘汰
     */
    DISTLOCK_ACQUIRE_FAILED(4, "很遗憾没抢到"),
    /**
     * Redis秒杀没抢到
     */
    REDIS_ERROR(3, "很遗憾没抢到"),
    SOLD_OUT(2, "已售罄"),
    SUCCESS(1, "恭喜你，抢到啦"),
    END(0, "活动已结束"),
    REPEAT_KILL(-1, "每人只限一次抢购机会"),
    /**
     * 运行时才能检测到的所有异常-系统异常
     */
    INNER_ERROR(-2, "很遗憾没抢到"),
    /**
     * md5错误的数据篡改
     */
    DATA_REWRITE(-3, "非法请求数据"),

    DB_CONCURRENCY_ERROR(-4, "很遗憾没抢到"),
    /**
     * 被AccessLimitService限流了
     */
    ACCESS_LIMIT(-5, "很遗憾没抢到");

    private int state;

    private String msg;

    SecKillState(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
