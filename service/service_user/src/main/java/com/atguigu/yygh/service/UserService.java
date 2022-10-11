package com.atguigu.yygh.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<UserInfo> {
    Map<String , Object > login(LoginVo loginVo);
    void setUserInfoParam(UserInfo userInfo);
}
