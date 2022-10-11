package com.atguigu.yygh.controller.user;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.prop.WxProperties;

import com.atguigu.yygh.service.UserService;
import com.atguigu.yygh.utils.HttpClientUtils;

import com.atguigu.yygh.utils.JwtHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/wx")
public class WXController {

    @Autowired
    WxProperties wxProperties;

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/getParam")
    public Result getParam(){
        return Result.ok().data("appid" , wxProperties.getAppid())
                .data("redirect_uri" , wxProperties.getRedirectUri())
                .data("appSecret" , wxProperties.getAppSecret())
                .data("scope" ,"snsapi_login")
                .data("state"  ,System.currentTimeMillis());

    }

    @RequestMapping("/callback")
    public String callback(String code , String state) throws Exception {

        StringBuilder sb = new StringBuilder( );
        sb.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
                .append(wxProperties.getAppid())
                .append("&secret=")
                .append(wxProperties.getAppSecret())
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");

        String  url = sb.toString();

        String s = HttpClientUtils.get(url);
        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");
        String accessToken = jsonObject.getString("access_token");


        String s1 = HttpClientUtils.get("https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken+"&openid=" + openid);
        JSONObject jsonObject1 = JSONObject.parseObject(s1);
        String nickname = jsonObject1.getString("nickname");

        UserInfo userInfo = userService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getOpenid, openid));
        if( userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setOpenid(openid);
            userInfo.setStatus(1);
            userInfo.setNickName(nickname);
            userService.save(userInfo);
        }

        Map<String , String > map = new HashMap<>();

        if (userInfo != null &&  userInfo.getStatus() != 1) {
            throw  new YYGHException(51000 , "用户已被锁定无法登录");
        }
        if( userInfo == null || userInfo.getPhone() == null )
            map.put("openid" , openid);
        else {
            map.put("openid", "");
            map.put("token", JwtHelper.createToken(userInfo.getId(), nickname));
        }

        map.put("name"  , nickname );


        return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+ URLEncoder.encode(map.get("name"),"utf-8");
    }
}
