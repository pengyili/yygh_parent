package com.atguigu.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;



@SpringBootApplication
@MapperScan("com.atguigu.yygh.hosp.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.yygh")
@EnableRabbit
public class HospitalSetApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalSetApplication.class , args);
    }
}
