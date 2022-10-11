package com.atguigu.yygh.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.service.WeiXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user/weixin")
public class WeiPayController {
    @Autowired
    WeiXinService weiXinService;
    @GetMapping("/pay/{orderId}")
    public Result pay(@PathVariable("orderId") Long orderId) throws Exception {

        String codeUrl = weiXinService.getCodeUrl(orderId);

        return Result.ok().data("codeUrl" , codeUrl);

    }
    @GetMapping("/queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId) throws Exception {

        weiXinService.queryPayStatus(orderId);

        return Result.ok();
    }
}
