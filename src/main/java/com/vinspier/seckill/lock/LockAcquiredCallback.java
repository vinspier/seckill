package com.vinspier.seckill.lock;


import javax.annotation.Nullable;

/**
 * 得到分布式锁的回调执行类
 *
 * 使用方式
 * 方式一：参数形式传入被调用者 符合条件时执行该任务
 *
 * 方式二：属性形式 做全局统一的处理 符合条件时执行该任务
 * */
@FunctionalInterface
public interface LockAcquiredCallback {
    void callback();
}
