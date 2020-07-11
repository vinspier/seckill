package com.vinspier.seckill.service.impl;

import com.vinspier.seckill.dao.PayOrderDao;
import com.vinspier.seckill.entity.PayOrder;
import com.vinspier.seckill.enums.PayOrderState;
import com.vinspier.seckill.service.PayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PayOrderServiceImpl implements PayOrderService {

    @Autowired
    private PayOrderDao payOrderDao;

    @Override
    public int createNew(long secKillId, long phone, int state) {
        PayOrder payOrder = new PayOrder();
        payOrder.setSeckillId(secKillId);
        payOrder.setUserPhone(phone);
        payOrder.setCreateTime(new Date());
        payOrder.setState(state);
        return payOrderDao.insert(payOrder);
    }
}