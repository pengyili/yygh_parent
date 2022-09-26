package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;

import com.atguigu.yygh.model.acl.User;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @PostMapping("/login")
    public Result login(@RequestBody User user){
        if(StringUtils.isNotEmpty(user.getUsername() )&& StringUtils.isNotEmpty(user.getPassword())){
            return Result.ok();
        }
        return Result.fail();
    }
    @GetMapping("/info")
    public Result info(String token){
        return Result.ok();
    }

}
