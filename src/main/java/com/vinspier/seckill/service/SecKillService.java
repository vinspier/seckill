package com.vinspier.seckill.service;

import com.vinspier.seckill.dto.Exposure;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.ResultCode;
import com.vinspier.seckill.enums.SecKillState;
import com.vinspier.seckill.mq.SecKillMsg;
import org.apache.ibatis.annotations.Param;

public interface SecKillService {

    SecKill findById(Long id);

    /** 从Redis或DB中 获取秒杀产品信息 并封装 */
    Exposure exposed(Long id);

    /** 处理 秒抢请求 */
    ResultCode grab(Long id,Long phone,String md5);

    /** 执行异步处理 */
    ResultCode executeGrabAsync(Long id, Long phone);

    /** 在redis中 处理真正的秒杀操作 */
    void doGrabInRedis(SecKillMsg msg);

    /** 插入抢购数据到DB中 并且更新秒杀商品信息 */
    void doModifySecKillInDB(Long id,Long phone);

    /** 查询秒杀状态 */
    SecKillState queryGrab(Long id, Long phone);

    /** 超时未支付订单 重新数量添加到库存中去 */
    int addInventoryForExpiredOrder(@Param("secKillId") long secKillId, @Param("inventory")int inventory);
}
