package com.vinspier.seckill.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 * curator 配置
 * */
@Configuration
public class ZooKeeperConfig {

    /**
     * 获取CuratorFramework
     * 使用curator 操作 zookeeper
     * 形参注入方式
     * */
    @Bean
    public CuratorFramework curatorFramework(ZooKeeperProperties zooKeeperProperties){
        //配置zookeeper连接的重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zooKeeperProperties.getBaseSleepTime(), zooKeeperProperties.getMaxRetries());

        //构建 CuratorFramework
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                        .connectString(zooKeeperProperties.getConnectStr())
                        .sessionTimeoutMs(zooKeeperProperties.getSessionTimeout())
                        .connectionTimeoutMs(zooKeeperProperties.getConnectTimeout())
                        .retryPolicy(retryPolicy)
                        .build();

        //连接 zookeeper
        curatorFramework.start();
        return curatorFramework;
    }

}
