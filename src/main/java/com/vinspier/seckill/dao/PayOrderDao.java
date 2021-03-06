package com.vinspier.seckill.dao;

import com.vinspier.seckill.entity.PayOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PayOrderDao extends Mapper<PayOrder> {

    /** 查询秒杀订单信息 携带商品信息 */
    PayOrder findBySecKillIdWithSecKill(@Param("secKillId")long secKillId,@Param("phone")long phone);

    /** 通过状态查询订单信息 */
    List<PayOrder> findListByState(@Param("state") int state);

    /**
     * 查询超时未支付订单
     * @Param originState 原始状态
     * @Param payedWaited 等待付款的超时时间 单位s
     * */
    List<PayOrder> findPayExpiredList(@Param("originState")int originState,@Param("payedWaited")long payedWaited);

    /** 未支付订单的状态设置
     * @Param originState 原始状态
     * @Param newState 更新后的状态
     * @Param payedWaited 等待付款的超时时间 单位s
     * */
    void payExpiredStateSet(@Param("originState")int originState,@Param("newState")int newState,@Param("payedWaited")long payedWaited);

    /**
     * 批量更新订单状态
     * */
    void batchUpdateState(@Param("payOrderList") List<PayOrder> payOrderList);
}
