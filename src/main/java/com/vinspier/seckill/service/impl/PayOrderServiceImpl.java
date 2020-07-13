package com.vinspier.seckill.service.impl;

import com.vinspier.seckill.dao.PayOrderDao;
import com.vinspier.seckill.entity.PayOrder;
import com.vinspier.seckill.enums.PayOrderState;
import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.service.PayOrderService;
import com.vinspier.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PayOrderServiceImpl implements PayOrderService {

    private final Logger logger = LoggerFactory.getLogger(PayOrderServiceImpl.class);

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
            StringBuilder msg = new StringBuilder();
            payExpiredList.forEach(o -> {
                o.setState(PayOrderState.INVALID.getState());
                msg.append("{").append("id:").append(o.getSeckillId()).append(",").append("phone:").append(o.getUserPhone()).append("}");
            });
            // 批量更新DB订单状态
            payOrderDao.batchUpdateState(payExpiredList);
            // 先根据不同商品的id 分组
            payOrderGroup = payExpiredList.stream().collect(Collectors.groupingBy(PayOrder::getSeckillId,Collectors.counting()));
            // 更新数据库 再 更新redis
            StringBuilder msg1 = new StringBuilder();
            payOrderGroup.forEach((secKillId,count) -> {
                secKillService.addInventoryForExpiredOrder(secKillId,count.intValue());
                redisTemplate.opsForValue().increment(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + secKillId,count);
                msg1.append("{").append("id:").append(secKillId).append(",").append("count:").append(count).append("}");
            });
            logger.info("定时执行 更新超时未支付订单状态 影响数=[{}] 数据=[{}]",payExpiredList.size(),msg.toString());
            logger.info("定时执行 更新DB和redis中的库存 数据=[{}]",msg1.toString());
        }
    }
}
