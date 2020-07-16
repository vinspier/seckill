package com.vinspier.seckill.controller;

import com.vinspier.seckill.config.ZooKeeperProperties;
import com.vinspier.seckill.enums.ResultCode;
import com.vinspier.seckill.handler.CustomizeResponse;
import com.vinspier.seckill.lock.ZooKeeperDistributedLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "zookeeper")
public class ZooKeeperLockController {

    @Autowired
    private ZooKeeperDistributedLockService zooKeeperDistributedLockService;
    @Autowired
    private ZooKeeperProperties zooKeeperProperties;

    /**
     * 提交抢商品请求
     * */
    @RequestMapping(value = "/lock")
    @ResponseBody
    public CustomizeResponse lock(){
        String path = zooKeeperProperties.getLockRoot() + "/lock";
        zooKeeperDistributedLockService.acquireLock(path,()->{
            System.out.println("acquire lock");
        });
        return CustomizeResponse.ok();
    }

}
