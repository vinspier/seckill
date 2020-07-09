package com.vinspier.seckill.service.impl;

import com.vinspier.seckill.config.CustomizeProperties;
import com.vinspier.seckill.dao.SecKillDao;
import com.vinspier.seckill.dto.Exposure;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.PrefixKey;
import com.vinspier.seckill.enums.ResultCode;
import com.vinspier.seckill.exception.CustomizeException;
import com.vinspier.seckill.service.SecKillService;
import com.vinspier.seckill.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private SecKillDao secKillDao;
    @Autowired
    private RedisTemplate<String,SecKill> seckillRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CustomizeProperties customizeProperties;

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
            seckillRedisTemplate.opsForValue().setIfAbsent(PrefixKey.SEC_KILLED_GOODS.getPrefix() + secKill.getSeckillId().toString(),secKill);
            redisTemplate.opsForValue().setIfAbsent(PrefixKey.SEC_KILLED_INVENTORY.getPrefix() + secKill.getSeckillId().toString(),secKill.getInventory());
            seckillRedisTemplate.opsForValue().setIfAbsent(PrefixKey.SEC_KILLED_GOODS + secKill.getSeckillId().toString(),secKill);
        }
        Exposure exposure = fetchExposure(secKill);
        return exposure;
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
