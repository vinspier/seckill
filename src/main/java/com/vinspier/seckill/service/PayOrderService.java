package com.vinspier.seckill.service;

import com.vinspier.seckill.entity.PayOrder;

import java.util.List;

public interface PayOrderService {

    /** 创建新秒杀信息 */
    int createNew(long secKillId,long phone,int state);

    /** 查询秒杀订单信息 携带商品信息 */
    PayOrder findBySecKillIdWithSecKill(long secKillId,long phone);

    /** 通过状态查询订单信息 */
    List<PayOrder> findListByState(int state);

    /**
     * 查询超时未支付订单
     * @Param originState 原始状态
     * @Param payedWaited 等待付款的超时时间 单位s
     * */
    List<PayOrder> findPayExpiredList(int originState,long payedWaited);

    /**
     * 未支付订单的状态设置
     * @Param originState 原始状态
     * @Param newState 更新后的状态
     * @Param payedWaited 等待付款的超时时间 单位s
     * */
    void payExpiredStateSet(int originState,int newState,long payedWaited);
}
