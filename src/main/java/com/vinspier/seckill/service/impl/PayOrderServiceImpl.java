package com.vinspier.seckill.service.impl;

import com.vinspier.seckill.dao.PayOrderDao;
import com.vinspier.seckill.entity.PayOrder;
import com.vinspier.seckill.enums.PayOrderState;
import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.service.PayOrderService;
import com.vinspier.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PayOrderServiceImpl implements PayOrderService {

    @Autowired
    private PayOrderDao payOrderDao;
    @Autowired
    private SecKillService secKillService;
    @Autowired
    private RedisTemplate redisTemplate;


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
    public List<PayOrder> findPayExpiredList(int originState, long payedWaited) {
        return payOrderDao.findPayExpiredList(originState,payedWaited);
    }

    @Override
    @Transactional
    public void payExpiredStateSet(int originState, int newState,long payedWaited) {
        List<PayOrder> payExpiredList = payOrderDao.findPayExpiredList(originState,payedWaited);
        Map<Long,Long> payOrderGroup;

        if (!CollectionUtils.isEmpty(payExpiredList)){
            // 更新状态为 超时状态
            payExpiredList.forEach(o -> o.setState(PayOrderState.INVALID.getState()));
            // ToDo 批量更新订单状态
            // 先根据不同商品的id 分组
            payOrderGroup = payExpiredList.stream().collect(Collectors.groupingBy(PayOrder::getSeckillId,Collectors.counting()));
            // 更新数据库 再 更新redis
            payOrderGroup.forEach((secKillId,count) -> {
                secKillService.addInventoryForExpiredOrder(secKillId,count.intValue());
                redisTemplate.opsForValue().increment(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + secKillId,count);
            });
        }

        payOrderDao.payExpiredStateSet(originState,newState,payedWaited);
    }
}
