package com.atguigu.yygh.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.vo.order.OrderMqVo;
import org.junit.jupiter.api.Test;


import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class BeanTest {
    @Test
    public void test() throws InvocationTargetException, IllegalAccessException {

        Map map = new HashMap() ;
        map.put("reservedNumber" , 10 );

        map.put("availableNumber"  , 10 ) ;

        String s = JSON.toJSONString(map);
        JSONObject jsonObject = JSON.parseObject(s);
//        Object availableNumber = jsonObject.get("availableNumber");


//        System.out.println("availableNumber = " + availableNumber);

        OrderMqVo orderMqVo = new OrderMqVo();


        BeanUtils.copyProperties( jsonObject ,map);



        System.out.println(orderMqVo);

    }
}
