package com.atguigu.yygh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.WeiXinProperties;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.service.OrderService;
import com.atguigu.yygh.service.PaymentInfoService;
import com.atguigu.yygh.service.WeiXinService;
import com.atguigu.yygh.utils.HttpClient;
import com.atguigu.yygh.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.github.wxpay.sdk.WXPayUtil;
import com.mysql.cj.xdevapi.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinServiceImpl implements WeiXinService {



    @Autowired
    private WeiXinProperties weiXinProperties;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentInfoService paymentInfoService;
    @Override
    public String getCodeUrl(Long orderId) throws Exception {
        OrderInfo orderInfo = orderService.getById(orderId);
        Map<String, String> map = new HashMap<>() ;
        HttpClient httpClient = new HttpClient(weiXinProperties.getUnifiedOrderUrl());
        map.put("appid"  , weiXinProperties.getAppid());
        map.put("mch_id" , weiXinProperties.getMchId() );
        map.put("nonce_str" ,  WXPayUtil.generateNonceStr());
        map.put("body" ,  "随便");
        map.put("out_trade_no" , orderInfo.getOutTradeNo());
        map.put("total_fee" , "1");
        map.put("trade_type" , "NATIVE");
        map.put("notify_url" , "https://www.baidu.com") ;


        Map<String, String> resultMap = weixinPayHelper(map, weiXinProperties.getUnifiedOrderUrl());

        String returnCode = resultMap.get("return_code");
        if(returnCode == null || !returnCode.equalsIgnoreCase("SUCCESS")){
            throw  new YYGHException(20000 , resultMap.get("return_msg"));
        }
        String resultCode = resultMap.get("result_code");
        if(resultCode == null || !resultCode.equalsIgnoreCase("SUCCESS"))
            throw new YYGHException(  20000, resultMap.get("err_code_des"));
        if(paymentInfoService.getOne( new LambdaUpdateWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId,orderId)) == null ) {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderId(orderId);
            paymentInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
            paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
            paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
            paymentInfo.setTotalAmount(orderInfo.getAmount());
            String subject = orderInfo.getReserveDate() + "|" + orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle();
            paymentInfo.setSubject(subject);
            paymentInfoService.save(paymentInfo);
        }




        return resultMap.get("code_url");
    }

    @Transactional
    @Override
    public void queryPayStatus(Long orderId) throws Exception {

        OrderInfo orderInfo = orderService.getById(orderId);
//        Map<String, String> appid = Map.of("appid", weiXinProperties.getAppid(),
//                "mch_id", weiXinProperties.getMchId(),
//                "out_trade_no", orderInfo.getOutTradeNo(),
//                "nonce_str", WXPayUtil.generateNonceStr()
//        );

        HashMap<String, String > sendMap = new HashMap<>();
        sendMap.put("appid", weiXinProperties.getAppid());
        sendMap.put("mch_id", weiXinProperties.getMchId());
        sendMap.put("out_trade_no", orderInfo.getOutTradeNo());
        sendMap.put("nonce_str", WXPayUtil.generateNonceStr());
        String s = HttpClientUtils.postParameters("http://localhost:9998//order/updatePayStatus", Map.of("hoscode", orderInfo.getHoscode(), "hosRecordId", orderInfo.getHosRecordId()));
        JSONObject jsonObject = JSON.parseObject(s);
        Integer code = jsonObject.getInteger("code");
        if(code != 200 )
            throw new YYGHException(code , jsonObject.getString("message"));
        Map<String, String> resultMap = weixinPayHelper(sendMap, weiXinProperties.getOrderQueryUrl());


        String returnCode = resultMap.get("return_code");
        if(returnCode == null || !returnCode.equalsIgnoreCase("SUCCESS")){
            throw  new YYGHException(23560 ,"支付中");
        }
        String resultCode = resultMap.get("result_code");
        if(resultCode == null || !resultCode.equalsIgnoreCase("SUCCESS"))
            throw new YYGHException(  23000, "支付中");
        String tradeState = resultMap.get("trade_state");

        if(tradeState == null || !tradeState.equalsIgnoreCase("SUCCESS")){
            throw new YYGHException(28800 , "支付中");
        }


        PaymentInfo paymentInfo =  new PaymentInfo();
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(JSON.toJSONString(resultMap));
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        boolean update = paymentInfoService.update(paymentInfo, new LambdaUpdateWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, orderId));
        if (!update)
            throw new YYGHException(22000 , "支付成功 ,支付信息更新失败 ,请联系人工解决");
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        boolean b = orderService.updateById(orderInfo);

        if(!b)
            throw new YYGHException(230000, "支付成功 , 订单信息更新失败 ,请联系人工解决");
    }
    Map<String , String > weixinPayHelper(Map<String , String > map  , String url ) throws Exception {
        HttpClient httpClient = new HttpClient(url);
        String s = WXPayUtil.generateSignedXml(map , weiXinProperties.getPartnerKey());
        httpClient.setXmlParam(s);
        httpClient.setHttps(true);
        httpClient.post();
        String content = httpClient.getContent();
        return WXPayUtil.xmlToMap(content);
    }
    public synchronized void pay(Map<String , String> resultMap){
        if(!resultMap.get("trade_state").equalsIgnoreCase("SUCCESS"))
            resultMap.put("trade_state" , "SUCCESS");
    }
}
