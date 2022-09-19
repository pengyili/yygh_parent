package com.atguigu.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.atguigu")
@MapperScan("com.atguigu.yygh.hosp.mapper")
public class HospitalSetApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalSetApplication.class , args);
    }
}
