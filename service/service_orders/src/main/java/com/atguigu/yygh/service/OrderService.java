package com.atguigu.yygh.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<OrderInfo> {

    Long savaOrder(String token, Long patientId, String scheduleId);

    void setOrderStatus(OrderInfo orderInfo);
}
