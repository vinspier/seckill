package com.vinspier.seckill.handler;

import com.vinspier.seckill.enums.ResultCode;

/**
 * @ClassName: ResponseSuccessTemplate
 * @Description: 自定义全局的成功返回异常
 * @Author:
 * @Date: 2020/3/19 10:22
 * @Version V1.0
 **/
public class CustomizeResponse<T> {

    private  int code;

    private String msg;

    private boolean success;

    private T data;

    private CustomizeResponse(int code, String msg, boolean success) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }

    private CustomizeResponse(T data, int code, String msg, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    public static CustomizeResponse ok(){
        return new CustomizeResponse(null,200,"操作成功",true);
    }

    public static CustomizeResponse ok(Object data){
        return new CustomizeResponse(data,200,"操作成功",true);
    }

    public static CustomizeResponse ok(ResultCode resultCode){
        return new CustomizeResponse(resultCode.getCode(),resultCode.getMsg(),true);
    }

    public static CustomizeResponse ok(ResultCode resultCode, Object data){
        return new CustomizeResponse(data,resultCode.getCode(),resultCode.getMsg(),true);
    }

    public static CustomizeResponse failed(Object data){
        return new CustomizeResponse(data,500,"操作失败",false);
    }

    public static CustomizeResponse failed(){
        return new CustomizeResponse(null,500,"操作失败",false);
    }

    public static CustomizeResponse failed(ResultCode resultCode){
        return new CustomizeResponse(resultCode.getCode(),resultCode.getMsg(),false);
    }

    public static CustomizeResponse failed(ResultCode resultCode, Object data){
        return new CustomizeResponse(data,resultCode.getCode(),resultCode.getMsg(),false);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseSuccessTemplate{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }
}
