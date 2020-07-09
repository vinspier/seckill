package com.vinspier.seckill.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 程序中需要的 自定义的配置参数
 * */
@ConfigurationProperties(prefix = "customize.")
@Component
@Data
public class CustomizeProperties {

    /** 暴露秒杀商品ID加密的 盐 */
    private String exposedSalt;

    /** 抢到秒杀商品 等待支付的最大时间 单位ms */
    private Long payedWaited;

}
