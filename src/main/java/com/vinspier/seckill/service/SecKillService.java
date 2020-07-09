package com.vinspier.seckill.service;

import com.vinspier.seckill.dto.Exposure;
import com.vinspier.seckill.entity.SecKill;

public interface SecKillService {

    SecKill findById(Long id);

    /** 从Redis或DB中 获取秒杀产品信息 并封装 */
    Exposure exposed(Long id);
}
