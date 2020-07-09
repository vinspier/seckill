package com.vinspier.seckill.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 对秒杀产品信息的包装
 * */
@Data
@ToString
public class Exposure implements Serializable {

    private static final long serialVersionUID = 3945522124434282652L;

    //是否开启秒杀
    private boolean exposed;

    //一种加密措施
    private String md5;

    //id
    private Long seckillId;

    //系统当前时间(毫秒)
    private Date now;

    //开启时间
    private Date start;

    //结束时间
    private Date end;
}
