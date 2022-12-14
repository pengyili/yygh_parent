package com.atguigu.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.atguigu.yygh.dict.mapper")
@EnableDiscoveryClient
public class DictApplication {
    public static void main(String[] args){
        SpringApplication.run(DictApplication.class, args);
    }
}
