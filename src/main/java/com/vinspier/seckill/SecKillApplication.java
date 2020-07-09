package com.vinspier.seckill;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.vinspier.seckill.dao"})
public class SecKillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillApplication.class,args);
    }
}
