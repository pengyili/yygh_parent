package com.atguigu.yygh.user;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "user-service"  ,path="/user/client")
public interface UserClient {

    @PostMapping("/getUserInfo")
    public UserInfo getUserInfo(@RequestHeader("token") String token);

    @GetMapping("/patient/{id}")
    public Patient getPatient(@PathVariable("id") Long id );
}
