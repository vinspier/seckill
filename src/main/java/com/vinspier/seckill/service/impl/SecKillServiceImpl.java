package com.vinspier.seckill.service.impl;

import com.vinspier.seckill.dao.SecKillDao;
import com.vinspier.seckill.entity.SecKill;
import com.vinspier.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private SecKillDao secKillDao;

    public SecKill findById(Long id){
        return secKillDao.selectByPrimaryKey(id);
    }

}
