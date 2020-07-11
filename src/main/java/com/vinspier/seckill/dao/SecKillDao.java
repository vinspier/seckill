package com.vinspier.seckill.dao;

import com.vinspier.seckill.entity.SecKill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface SecKillDao extends Mapper<SecKill> {

    /** 更新数据版本和库存 */
    int reduceInventory(@Param("secKillId") long secKillId, @Param("oldVersion")long oldVersion, @Param("newVersion")long newVersion);
}
