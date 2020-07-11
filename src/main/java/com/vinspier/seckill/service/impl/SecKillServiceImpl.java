package com.vinspier.seckill.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.vinspier.seckill.config.CustomizeProperties;
import com.vinspier.seckill.dao.SecKillDao;
import com.vinspier.seckill.dto.Exposure;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PayOrderState;
import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.enums.ResultCode;
import com.vinspier.seckill.enums.SecKillState;
import com.vinspier.seckill.exception.CustomizeException;
import com.vinspier.seckill.mq.RabbitKeys;
import com.vinspier.seckill.mq.SecKillMsg;
import com.vinspier.seckill.service.PayOrderService;
import com.vinspier.seckill.service.SecKillService;
import com.vinspier.seckill.util.DateUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private PayOrderService payOrderService;
    @Autowired
    private RedisTemplate<String,SecKill> seckillRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CustomizeProperties customizeProperties;
    @Autowired
    private RateLimiter rateLimiter;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SecKillService secKillService;

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
        if (DateUtil.before(secKill.getStartTime(),current)){
            logger.info("secKill activity is not being starting [ secKillId={},phone={} ] ",id,phone);
            return ResultCode.UN_START;
        }
        if (DateUtil.after(secKill.getEndTime(),current)){
            logger.info("secKill activity had been stopped[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.END;
        }
        // 以下这两步骤 保证了同一个用户 只会发送一条消息给MQ队列
        // 判断是否重在排队中
        if (redisTemplate.opsForSet().isMember(PrefixKey.SEC_KILLED_PRE_GRABS.getPrefix(),id + "@" + phone)){
            logger.info("you got an chance and is on the queue[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.ENQUEUE_PRE_SECKILL;
        }
        // 判断是否重复秒杀
        if (redisTemplate.opsForSet().isMember(PrefixKey.SEC_KILLED_BOUGHT_USERS.getPrefix() + id,phone)){
            logger.info("sorry,there is only once chance for every one[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.REPEAT_KILL;
        }
        // 验证redis中的库存是否为0
        Integer inventory = (Integer)redisTemplate.opsForValue().get(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + id);
        if (inventory.compareTo(0) <= 0){
            logger.info("sorry secKill product has sold out[ secKillId={},phone={} ] ",id,phone);
            return ResultCode.SOLD_OUT;
        }
        return executeGrabAsync(id,phone);
    }

    /**
     * 处理真正的抢商品流程
     * 发送消息给MQ 处理redis中库存数据
     *
     * */
    private ResultCode executeGrabAsync(Long id, Long phone){
        SecKillMsg secKillMsg = new SecKillMsg();
        secKillMsg.setSecKillId(id);
        secKillMsg.setUserPhone(phone);
        rabbitTemplate.convertAndSend(RabbitKeys.EXCHANGE_NAME,RabbitKeys.GRAB_KEY,secKillMsg);
        // 消息发送MQ成功，该用户被认为是抢到了预购机会 有排队资格
        redisTemplate.opsForSet().add(PrefixKey.SEC_KILLED_PRE_GRABS.getPrefix(), secKillMsg.getSecKillId() + "@" + secKillMsg.getUserPhone());
        return ResultCode.ENQUEUE_PRE_SECKILL;
    }

    /**
     * 插入秒杀订单到数据库
     * 成功后 在redis中执行秒杀的数据更新操作
     *
     * */
    @Override
    @Transactional
    public void doGrabInRedis(SecKillMsg msg) {
        Long id = msg.getSecKillId();
        Long phone = msg.getUserPhone();
        // 再次判断是否重复秒杀
        if (redisTemplate.opsForSet().isMember(PrefixKey.SEC_KILLED_BOUGHT_USERS.getPrefix() + id,phone)){
            logger.info("sorry,there is only once chance for every one[ secKillId={},phone={} ] ",id,phone);
            throw new CustomizeException(ResultCode.REPEAT_KILL);
        }
        // 再次验证redis中的库存是否为0
        Integer inventory = (Integer)redisTemplate.opsForValue().get(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + id);
        if (inventory.compareTo(0) <= 0){
            logger.info("sorry secKill product has sold out[ secKillId={},phone={} ] ",id,phone);
            throw new CustomizeException(ResultCode.SOLD_OUT);
        }
        // 判断是否在活动时间内
        SecKill secKill = seckillRedisTemplate.opsForValue().get(PrefixKey.SEC_KILLED_GOODS.getPrefix() + id);
        Date current = new Date();
        if (DateUtil.before(secKill.getStartTime(),current)){
            logger.info("secKill activity is not being starting [ secKillId={},phone={} ] ",id,phone);
            throw new CustomizeException(ResultCode.UN_START);
        }
        if (DateUtil.after(secKill.getEndTime(),current)){
            logger.info("secKill activity had been stopped[ secKillId={},phone={} ] ",id,phone);
            throw new CustomizeException(ResultCode.END);
        }
        // 模拟MQ消费出错 查看消息队列如何处理
//        throw new CustomizeException(ResultCode.SERVER_UNKNOWN_ERROR);
        // 现在DB中产生数据
        secKillService.doModifySecKillInDB(id,phone);
        // 更新redis中的库存
        redisTemplate.opsForValue().decrement(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + id);
        // 添加用户到 抢到秒杀的集合key中
        redisTemplate.opsForSet().add(PrefixKey.SEC_KILLED_BOUGHT_USERS.getPrefix() + id,phone);
    }

    @Override
    @Transactional
    public void doModifySecKillInDB(Long id,Long phone) {
        int createNew = payOrderService.createNew(id,phone, PayOrderState.GRAB_SECUSS.getState());
        if (createNew <= 0){
            logger.info("sorry,there is only once chance for every one[ secKillId={},phone={} ] ",id,phone);
            throw new CustomizeException(ResultCode.REPEAT_KILL);
        }
        SecKill secKill = secKillDao.selectByPrimaryKey(id);
        Date current = new Date();
        if (DateUtil.before(secKill.getStartTime(),current)){
            logger.info("secKill activity is not being starting [ secKillId={},phone={} ] ",id,phone);
            throw new CustomizeException(ResultCode.UN_START);
        }
        if (DateUtil.after(secKill.getEndTime(),current)){
            logger.info("secKill activity had been stopped[ secKillId={},phone={} ] ",id,phone);
            throw new CustomizeException(ResultCode.END);
        }
        int reduceState =  secKillDao.reduceInventory(id,secKill.getVersion(),secKill.getVersion() + 1);
        if (reduceState <= 0){
            throw new CustomizeException(ResultCode.SERVER_UNKNOWN_ERROR);
        }
    }

    @Override
    public SecKillState queryGrab(Long id, Long phone) {
        if (redisTemplate.opsForSet().isMember(PrefixKey.SEC_KILLED_BOUGHT_USERS.getPrefix() + id,phone)){
            return SecKillState.SUCCESS;
        }else{
            if (redisTemplate.opsForSet().isMember(PrefixKey.SEC_KILLED_PRE_GRABS,id + "@" + phone)){
                return SecKillState.QUEUE;
            }
            return SecKillState.FAILED;
        }

    }

    /**
     * 封装查询出来的秒杀数据
     * */
    private Exposure fetchExposure(SecKill secKill){
        Exposure exposure = new Exposure();
        exposure.setSeckillId(secKill.getSeckillId());
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
