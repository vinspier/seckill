package com.vinspier.seckill.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 参加活动秒杀的商品信息
 */
@Data
@ToString
@Table(name = "sec_kill")
public class SecKill implements Serializable {
    private static final long serialVersionUID = -5161466177783266963L;
    // 商品id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seckillId;
    // 名称
    private String name;
    // 库存
    private Integer inventory;
    // 秒杀开始时间
    private Date startTime;
    // 秒杀结束时间
    private Date endTime;
    // 商品创建时间
    private Date createTime;
    // 版本
    private Long version;

}
