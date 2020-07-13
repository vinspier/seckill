package com.vinspier.seckill.enums;


public enum PayOrderState {


    INVALID(-1),
    GRAB_SUCCEUSS(0),
    UN_DELIVER(1),
    DELIVERING(2),
    DONE(3);

    private int state;

    PayOrderState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
