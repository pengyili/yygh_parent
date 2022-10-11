package com.atguigu.yygh.service.impl;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.mapper.UserMapper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.service.UserService;
import com.atguigu.yygh.utils.JwtHelper;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, UserInfo> implements  UserService {


    @Autowired
    RedisTemplate<String ,String> redisTemplate;
    @Override
    public Map<String , Object> login(LoginVo loginVo) {

        if(StringUtils.isBlank(loginVo.getPhone() ))
            throw new YYGHException(51110 , "输入的手机号为空");
        UserInfo userInfo = baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, loginVo.getPhone()));


        if(StringUtils.isEmpty(loginVo.getCode())|| !loginVo.getCode().equals(redisTemplate.opsForValue().get(loginVo.getPhone())))
            throw new YYGHException(21110 , "验证码错误");

        if(userInfo == null ){
            userInfo = new UserInfo();
            BeanUtils.copyProperties(loginVo , userInfo);
            userInfo.setStatus(1);
            this.save(userInfo);
        }
        if(StringUtils.isNotBlank(loginVo.getOpenid())){
            UserInfo userInfo1 = baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getOpenid, loginVo.getOpenid()));
                baseMapper.deleteById(userInfo1.getId()) ;
                userInfo.setNickName(userInfo1.getNickName());
                userInfo.setOpenid(userInfo1.getOpenid());
                baseMapper.updateById(userInfo);
        }

        if (userInfo.getStatus() != 1) {
            throw  new YYGHException(51000 , "用户已被锁定无法登录");
        }
        String name = null ;
        if(StringUtils.isNotBlank(userInfo.getNickName()))
            name = userInfo.getNickName();
        else
            name = userInfo.getPhone();

        String token = JwtHelper.createToken(userInfo.getId(), name);
        return Map.of("name" ,name , "token" , token );
    }

    @Override
    public void setUserInfoParam(UserInfo userInfo) {
        userInfo.setParam(Map.of("authStatusString" ,
                userInfo.getAuthStatus() ==  1 ? "正在认证" :userInfo.getAuthStatus() ==  0 ? "未认证" : "已认证" ,
                "statusString" ,
                userInfo.getStatus() == 1 ? "正常" : "锁定中")
        );

    }
}
