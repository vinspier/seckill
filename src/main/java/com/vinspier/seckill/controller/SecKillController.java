package com.vinspier.seckill.controller;

import com.vinspier.seckill.enums.ResultCode;
import com.vinspier.seckill.handler.CustomizeResponse;
import com.vinspier.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/secKill")
public class SecKillController {

    @Autowired
    private SecKillService secKillService;


    /**
     * 查询秒杀商品 是否在活动时间内
     * 获取md5
     * */
    @RequestMapping(value = "/exposed/{secKillId}")
    @ResponseBody
    public CustomizeResponse exposed(@PathVariable Long secKillId){
        return CustomizeResponse.ok(secKillService.exposed(secKillId));
    }

    /**
     * 提交抢商品请求
     * */
    @RequestMapping(value = "/grab/{secKillId}/{phone}/{md5}")
    @ResponseBody
    public CustomizeResponse grab(@PathVariable("secKillId") Long secKillId,@PathVariable("phone") Long phone,@PathVariable("md5") String md5){
        // 伪代码 验证phone合法性 实际中可以用户的id
        if (phone == null){
            return CustomizeResponse.ok(ResultCode.USER_NOT_EXIST);
        }
        return CustomizeResponse.ok(secKillService.grab(secKillId,phone,md5));
    }

    /**
     * 查询秒杀状态
     * */
    @RequestMapping(value = "/queryGrab/{secKillId}/{phone}")
    @ResponseBody
    public CustomizeResponse queryGrab(@PathVariable("secKillId") Long secKillId,@PathVariable("phone") Long phone){
        return CustomizeResponse.ok(secKillService.queryGrab(secKillId,phone));
    }

}
