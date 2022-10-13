package com.atguigu.yygh.client;

import com.atguigu.yygh.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient( value = "order-service" , path = "/order/client")
public interface OrderClient {
    @GetMapping("/smsOrder")
    List<OrderInfo> getOrderList();
}
