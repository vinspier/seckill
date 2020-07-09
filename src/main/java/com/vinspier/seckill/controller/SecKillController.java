package com.vinspier.seckill.controller;

import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.enums.ResultCode;
import com.vinspier.seckill.handler.CustomizeResponse;
import com.vinspier.seckill.service.SecKillService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/secKill")
public class SecKillController {

    private SecKillService secKillService;


    /**
     * 查询秒杀商品 是否在活动时间内
     * */
    @RequestMapping(value = "/exposed/{secKillId}")
    public CustomizeResponse exposed(@PathVariable Long secKillId){
        return CustomizeResponse.ok(secKillService.exposed(secKillId));
    }

}
