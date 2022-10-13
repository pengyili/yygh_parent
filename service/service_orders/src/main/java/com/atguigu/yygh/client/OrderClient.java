package com.atguigu.yygh.client;

import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order/client")
public class OrderClient {
    @Autowired
    OrderService orderService;
    @GetMapping("/smsOrder")
    public List<OrderInfo> getOrderList(){
        String s = DateTime.now().toString("yyyy-HH-dd");
        Date date = new DateTime(s).toDate();
       return orderService.list(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getReserveDate ,date).ne(OrderInfo::getOrderStatus ,OrderStatusEnum.CANCLE.getStatus()));
    }
}
