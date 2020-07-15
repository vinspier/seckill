package com.vinspier.seckill.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



/**
 * 连接zookeeper的客户端配置
 * */
@ConfigurationProperties(prefix = "zookeeper")
@Component
@Data
public class ZooKeeperProperties {

    /** 锁根节点 */
    String lockRoot;
    /** 每次重试时间间隔，单位毫秒 */
    int baseSleepTime;
    /** 重试次数 */
    int maxRetries;
    /** zookeeper服务连接id与端口 */
    String connectStr;
    /** 会话超时时间，单位毫秒 */
    int sessionTimeout;
    /** 连接创建超时时间，单位毫秒 */
    int connectTimeout;
    /** 获取锁等待时间，单位毫秒 */
    long lockAcquireTimeout;

}
