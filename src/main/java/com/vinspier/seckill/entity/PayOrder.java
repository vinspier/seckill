package com.vinspier.seckill.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 秒杀成功的订单信息
 */
@Data
@ToString
@Table(name = "pay_order")
public class PayOrder {

    // id
    @Id
    private Long seckillId;

    // 手机号
    private Long userPhone;

    // 状态标示:-1:无效 0:成功 1:已付款 2:已发货
    private Integer state;

    // 创建时间
    private Date createTime;

    // 多对一
    private SecKill secKill;

}
