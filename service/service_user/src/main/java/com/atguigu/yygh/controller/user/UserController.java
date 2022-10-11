package com.atguigu.yygh.controller.user;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.service.UserService;
import com.atguigu.yygh.utils.JwtHelper;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/info")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo ){

       Map<String , Object > map =  userService.login(loginVo);

       return Result.ok().data(map);
    }

    @PostMapping("/auth")
    public   Result auth(@RequestBody UserAuthVo userAuthVo ,@RequestHeader String token ){
        Long userId = JwtHelper.getUserId(token);
        UserInfo userInfo = new UserInfo() ;
        BeanUtils.copyProperties(userAuthVo ,userInfo );
        userInfo.setId(userId);
        userInfo.setAuthStatus(1);
        userService.updateById(userInfo);

        return Result.ok();
    }
    @GetMapping("/auth/getUserInfo")
    public  Result authInfo(@RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        UserInfo byId = userService.getById(userId);
        byId.setParam(Map.of("authStatusString" , byId.getAuthStatus() ==  2 ? "已完成认证" : byId.getAuthStatus() == 1 ? "认证中" : "为认证" ));
        return Result.ok().data("item" , byId);
    }

}

