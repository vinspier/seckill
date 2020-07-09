package com.vinspier.seckill.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinspier.seckill.entity.SecKill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfigure {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RedisSerializer<String> redisSerializer = new StringRedisSerializer();
    private static final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    private static final Jackson2JsonRedisSerializer<SecKill> secKillJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(SecKill.class);

    /**
     * 自定义redis序列化的方式
     * 默认采用的是JDK的方式
     * */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        /** 使用ObjectMapper反序列化value */
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 自定义redis序列化的方式
     * 默认采用的是JDK的方式
     * */
    @Bean(name = "seckillRedisTemplate")
    public RedisTemplate<String,SecKill> seckillRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String,SecKill> redisTemplate = new RedisTemplate();
        /** 使用ObjectMapper反序列化value */
        secKillJackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(secKillJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(secKillJackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

}
