package com.vinspier.seckill.enums;

/**
 * 抢购秒杀最后的状态
 * */
public enum  SecKillState {

    FAILED(2, "秒杀失败"),
    SUCCESS(1, "秒杀成功"),
    QUEUE(0, "排队中"),
    NOT_EXISTED(1, "没有秒杀记录"),;

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
