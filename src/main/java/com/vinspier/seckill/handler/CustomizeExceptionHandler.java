package com.vinspier.seckill.handler;

import com.alibaba.fastjson.JSONObject;
import com.vinspier.seckill.exception.CustomizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: CustomizeExceptionHandler
 * @Description: 自定义全局异常处理类
 * @Author:
 * @Date: 2020/3/19 11:29
 * @Version V1.0
 **/

@ControllerAdvice
public class CustomizeExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeExceptionHandler.class);

    /**
     * 处理程序抛出的非自定义异常
     * */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String handleException(Exception e){
        logger.error(e.getMessage(),e);
        return JSONObject.toJSONString(CustomizeResponse.failed(null));
    }

    /**
     * 处理程序抛出的非自定义的CustomizeException异常
     * */
    @ExceptionHandler(value = CustomizeException.class)
    @ResponseBody
    public String handleUserException(CustomizeException e){
        logger.error(e.getMessage(),e);
        return JSONObject.toJSONString(CustomizeResponse.failed(e.getResultCode()));
    }

}
