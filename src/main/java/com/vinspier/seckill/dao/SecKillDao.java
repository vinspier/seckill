package com.vinspier.seckill.dao;

import com.vinspier.seckill.entity.SecKill;
import tk.mybatis.mapper.common.Mapper;

public interface SecKillDao extends Mapper<SecKill> {

    /** 更新数据版本和库存 */
    int reduceInventory(long secKillId,long oldVersion,long newVersion);
}
