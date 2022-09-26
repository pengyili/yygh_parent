package com.atguigu.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.atguigu.yygh.api.mapper")
public class APIAppliation {
    public static void main(String[] args) {
        SpringApplication.run(APIAppliation.class , args) ;
    }
}
