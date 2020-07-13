package com.vinspier.seckill.task;

import com.vinspier.seckill.config.CustomizeProperties;
import com.vinspier.seckill.enums.PayOrderState;
import com.vinspier.seckill.enums.SecKillState;
import com.vinspier.seckill.service.PayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单信息的定时审核，修改状态
 *
 * */
@Component
public class PayOrderCheckTask {

    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private CustomizeProperties customizeProperties;



    /**
     * 项目启动后 5分钟后重启
     * 每隔 15min 钟 轮询查一次
     * */
    @Scheduled(initialDelay =  1000 * 60 * 5,fixedDelay = 1000 * 60 * 15)
    public void payDelayCheck(){
        payOrderService.payExpiredStateSet(PayOrderState.GRAB_SUCCEUSS.getState(),PayOrderState.INVALID.getState(),customizeProperties.getPayedWaited());
    }

}
