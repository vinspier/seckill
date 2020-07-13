package com.vinspier.seckill.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用google guava 的RateLimiter 限流
 * */

@Configuration
public class RateLimiterConfigure {

    @Autowired
    private CustomizeProperties customizeProperties;

    @Bean
    public RateLimiter rateLimiter(){
        RateLimiter rateLimiter = RateLimiter.create(customizeProperties.getRateLimited());
        return rateLimiter;
    }

}
