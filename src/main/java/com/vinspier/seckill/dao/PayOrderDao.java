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

}
