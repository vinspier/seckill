package com.vinspier.seckill.exception;

import com.vinspier.seckill.enums.ResultCode;

/**
 * @ClassName: CustomizeException
 * @Description: 自定义的异常 用户程序中抛出
 * 待统一全局异常返回信息处理
 * @Author:
 * @Date: 2020/3/19 11:22
 * @Version V1.0
 **/
public class CustomizeException extends RuntimeException{

    private ResultCode resultCode;

    public CustomizeException(ResultCode resultCode) {
        super();
        this.resultCode = resultCode;
    }

    public CustomizeException(String message, ResultCode resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public CustomizeException(String message, Throwable cause, ResultCode resultCode) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
