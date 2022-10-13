package com.atguigu.yygh.controller;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.utils.HttpUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@RestController
@RequestMapping("/user/sms")
public class SMSController {


    @Autowired
    RedisTemplate<String ,String > redisTemplate;


    private final String host = "http://dingxin.market.alicloudapi.com";
    private final String path = "/dx/sendSms";

    private final String appcode = "2ab8826042df4e9bade4a29f6d9a4907";

    @GetMapping("/{phone}")
    public Result getCode(@PathVariable("phone") String phone) {


        if (StringUtils.isEmpty(phone))
            throw new YYGHException(20001, "手机号为空");

        if(!StringUtils.isEmpty(redisTemplate.opsForValue().get(phone)))
            return Result.ok();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(RandomGenerator.getDefault().nextInt(0 , 10 ))
                .append(RandomGenerator.getDefault().nextInt(0 , 10 ))
                .append(RandomGenerator.getDefault().nextInt(0 , 10 ))
                .append(RandomGenerator.getDefault().nextInt(0 , 10 ));

        String  code   = stringBuilder.toString();
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param","code:" + code );
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();

        redisTemplate.opsForValue().set(phone , code , 10 , TimeUnit.DAYS);


        try {
//            /**
//             * 重要提示如下:
//             * HttpUtils请从
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
//             * 下载
//             *
//             * 相应的依赖请参照
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
//             */
            HttpResponse response = HttpUtils.doPost(host, path, "POST", headers, querys, bodys);
            System.out.println(response.toString());
            return Result.ok();
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

}
