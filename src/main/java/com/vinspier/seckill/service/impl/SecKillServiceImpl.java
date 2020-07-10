package com.vinspier.seckill.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.vinspier.seckill.config.CustomizeProperties;
import com.vinspier.seckill.dao.SecKillDao;
import com.vinspier.seckill.dto.Exposure;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.enums.ResultCode;
import com.vinspier.seckill.exception.CustomizeException;
import com.vinspier.seckill.service.SecKillService;
import com.vinspier.seckill.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SecKillServiceImpl implements SecKillService {

    private final Logger logger = LoggerFactory.getLogger(SecKillServiceImpl.class);

    @Autowired
    private SecKillDao secKillDao;
    @Autowired
    private RedisTemplate<String,SecKill> seckillRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CustomizeProperties customizeProperties;
    @Autowired
    private RateLimiter rateLimiter;

    public SecKill findById(Long id){
        return secKillDao.selectByPrimaryKey(id);
    }

    /**
     * 查询 是否活动开始
     * */
    @Override
    @Transactional
    public Exposure exposed(Long id) {
        SecKill secKill = seckillRedisTemplate.opsForValue().get(PrefixKey.SEC_KILLED_GOODS + id.toString());
        // redis中 不存在
        if (secKill == null){
            secKill = secKillDao.selectByPrimaryKey(id);
            // 未获取到信息 抛出异常
            if (secKill == null){
                throw new CustomizeException(ResultCode.FETCH_DATA_NONE);
            }
            // 存入redis中
            seckillRedisTemplate.opsForValue().setIfAbsent(PrefixKey.SEC_KILLED_GOODS.getPrefix() + secKill.getSeckillId(),secKill);
            redisTemplate.opsForValue().setIfAbsent(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + secKill.getSeckillId(),secKill.getInventory());
            redisTemplate.opsForSet().add(PrefixKey.SEC_KILLED_IDS.getPrefix(),secKill.getSeckillId());
        }
        Exposure exposure = fetchExposure(secKill);
        return exposure;
    }

    /**
     * 1、先判断 是否限流
     * 2、md5 校验 数据是否被修改过
     * 3、判断是否重复秒杀
     * 4、判断是否在活动时间内
     * 5、验证redis中的库存是否为0
     * */
    @Override
    @Transactional
    public ResultCode grab(Long id, Long phone, String md5) {
        // 先获取限流机会
        if (!rateLimiter.tryAcquire(customizeProperties.getRateLimiterWait(), TimeUnit.MILLISECONDS)){
            logger.info("access limited by google rateLimiter[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.ACCESS_LIMIT;
        }
        // 判断数据是否被修改过
        if (!StringUtils.hasText(md5) || !md5.equals(DigestUtils.md5DigestAsHex((id + customizeProperties.getExposedSalt()).getBytes()))){
            logger.info("data has Tampered by invalidate user[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.DATA_REWRITE;
        }

        // 判断是否在活动时间内
        SecKill secKill = seckillRedisTemplate.opsForValue().get(PrefixKey.SEC_KILLED_GOODS.getPrefix() + id);
        Date current = new Date();
        if (!DateUtil.before(secKill.getStartTime(),current)){
            logger.info("secKill activity is not being starting [ secKillId={},phone={} ] ",id,phone);
            return ResultCode.UN_START;
        }
        if (!DateUtil.after(secKill.getEndTime(),current)){
            logger.info("secKill activity had been stopped[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.END;
        }
        // 判断是否重复秒杀
        if (redisTemplate.opsForSet().isMember(PrefixKey.SEC_KILLED_BOUGHT_USERS.getPrefix() + id,phone)){
            logger.info("sorry,there is only once chance for every one[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.REPEAT_KILL;
        }
        // 验证redis中的库存是否为0
        Integer inventory = (Integer)redisTemplate.opsForValue().get(PrefixKey.SEC_KILLED_INVENTORY.getPrefix());
        if (inventory.compareTo(0) <= 0){
            logger.info("sorry secKill product has sold out[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.SOLD_OUT;
        }
        return executeGrabAsync(id,phone,md5);
    }

    /**
     * 处理真正的抢商品流程
     * 3、发送消息给MQ 处理redis中库存数据 结合分布式锁
     *
     * */
    private ResultCode executeGrabAsync(Long id, Long phone, String md5){
        // ToDo 发送排队消息给MQ队列
        return ResultCode.ENQUEUE_PRE_SECKILL;
    }

    /**
     * 封装查询出来的秒杀数据
     * */
    private Exposure fetchExposure(SecKill secKill){
        Exposure exposure = new Exposure();
        exposure.setStart(secKill.getStartTime());
        exposure.setEnd(secKill.getEndTime());
        exposure.setNow(new Date());
        exposure.setExposed(DateUtil.between(exposure.getStart(),exposure.getEnd(),exposure.getNow()));
        /**
         * id + salt加密返回客户端
         * 下次请求时携带该MD5字符串 防止信息被截取修改
         * */
        exposure.setMd5(DigestUtils.md5DigestAsHex((secKill.getSeckillId() + customizeProperties.getExposedSalt()).getBytes()));
        return exposure;
    }

}
