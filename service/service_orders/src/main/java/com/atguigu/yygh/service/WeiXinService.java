package com.atguigu.yygh.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;

import java.io.IOException;
import java.util.Map;

public interface WeiXinService {
    String getCodeUrl(Long orderId) throws IOException, Exception;

    void queryPayStatus(Long orderId) throws Exception;

    Map<String, String> refunds(OrderInfo orderInfo) throws Exception;

    boolean queryRefunds(PaymentInfo paymentInfo, String outRefundNo);
}
