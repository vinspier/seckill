package com.vinspier.seckill.enums;

public enum ResultCode {

    /** 秒杀的状态 */
    ENQUEUE_PRE_SECKILL(6, "排队中..."),
    /** 释放分布式锁失败，秒杀被淘汰*/
    DISTLOCK_RELEASE_FAILED(5, "很遗憾没抢到"),
    /** 获取分布式锁失败，秒杀被淘汰 */
    DISTLOCK_ACQUIRE_FAILED(4, "很遗憾没抢到"),
    /** Redis秒杀没抢到 */
    REDIS_ERROR(3, "很遗憾没抢到"),
    SOLD_OUT(2, "已售罄"),
    SUCCESS(1, "恭喜你，抢到啦"),
    END(0, "活动已结束"),
    UN_START(-6, "活动未开始"),
    REPEAT_KILL(-1, "每人只限一次抢购机会"),
    /** 运行时才能检测到的所有异常-系统异常 */
    INNER_ERROR(-2, "很遗憾没抢到"),
    /** md5错误的数据篡改 */
    DATA_REWRITE(-3, "非法请求数据"),
    DB_CONCURRENCY_ERROR(-4, "很遗憾没抢到"),
    /** 被AccessLimitService限流了 */
    ACCESS_LIMIT(-5, "很遗憾没抢到"),


    /** 统一的操作流状态 */
    SERVER_SUCCESS(200,"操作成功"),
    FETCH_DATA_NONE(204,"操作成功，未获取到对应数据"),
    USER_NOT_EXIST(205,"用户不存在"),
    PARAMETER_WRONG(400,"参数校验异常"),
    USERNAME_PASSWORD_WRONG(401,"账号密码或密码错误"),
    ACCESS_TOKEN_NONE(405,"缺少身份认证令牌token"),
    SERVER_UNKNOWN_ERROR(500,"系统发生异常")
    ;

    private int code;

    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
