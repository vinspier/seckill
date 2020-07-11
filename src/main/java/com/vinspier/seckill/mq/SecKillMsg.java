package com.vinspier.seckill.mq;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class SecKillMsg implements Serializable{

    private static final long serialVersionUID = -7873549007596966153L;
    /** 秒杀商品ID */
    private long secKillId;

    /** 用户手机号码 */
    private long userPhone;

}
