package com.vinspier.seckill.service;

import com.vinspier.seckill.dto.Exposure;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.ResultCode;

public interface SecKillService {

    SecKill findById(Long id);

    /** 从Redis或DB中 获取秒杀产品信息 并封装 */
    Exposure exposed(Long id);

    /** 处理 秒抢请求 */
    ResultCode grab(Long id,Long phone,String md5);


}
