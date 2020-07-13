package com.vinspier.seckill.service.impl;

import com.vinspier.seckill.dao.PayOrderDao;
import com.vinspier.seckill.entity.PayOrder;
import com.vinspier.seckill.enums.PayOrderState;
import com.vinspier.seckill.service.PayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    @Override
    public PayOrder findBySecKillIdWithSecKill(long secKillId, long phone) {
        return payOrderDao.findBySecKillIdWithSecKill(secKillId,phone);
    }

    @Override
    public List<PayOrder> findListByState(int state) {
        return payOrderDao.findListByState(state);
    }

    @Override
    @Transactional
    public void payDelayStateSet(int originState, int newState,long payedWaited) {
        payOrderDao.payDelayStateSet(originState,newState,payedWaited);
    }
}
