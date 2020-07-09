package com.vinspier.seckill.enums;

public enum ResultCode {

    SERVER_SUCCESS(200,"操作成功"),
    FETCH_DATA_NONE(204,"操作成功，未获取到对应数据"),
    USER_NOT_EXIST(205,"用户不存在"),
    PARAMETER_WRONG(400,"参数校验异常"),
    USERNAME_PASSWORD_WRONG(401,"账号密码或密码错误"),
    ACCESS_TOKEN_NONE(405,"缺少身份认证令牌access token"),
    SERVER_UNKNOW_ERROR(500,"系统发生异常")
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
