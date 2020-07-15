package com.vinspier.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redission客户端配置类
 * */
@Configuration
public class RedissionConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedissionConfig.class);

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redissonClient;

        Config config = new Config();
        String url = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        config.useSingleServer().setAddress(url)
                .setDatabase(redisProperties.getDatabase());
                // .setPassword(redisProperties.getPassword())

        try {
            redissonClient = Redisson.create(config);
            return redissonClient;
        } catch (Exception e) {
            logger.error("RedissonClient init redis url:[{}], Exception:", url, e);
            return null;
        }
    }
}
