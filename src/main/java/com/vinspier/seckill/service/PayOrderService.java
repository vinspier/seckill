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
}
