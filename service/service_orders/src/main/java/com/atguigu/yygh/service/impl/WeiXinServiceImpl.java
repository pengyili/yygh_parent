package com.atguigu.yygh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.WeiXinProperties;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.service.OrderService;
import com.atguigu.yygh.service.PaymentInfoService;
import com.atguigu.yygh.service.RefundInfoService;
import com.atguigu.yygh.service.WeiXinService;
import com.atguigu.yygh.utils.HttpClient;
import com.atguigu.yygh.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.github.wxpay.sdk.WXPayUtil;
import com.mysql.cj.protocol.a.authentication.Sha256PasswordPlugin;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class WeiXinServiceImpl implements WeiXinService {



    @Autowired
    private WeiXinProperties weiXinProperties;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private RefundInfoService refundInfoService;
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


        Map<String, String> resultMap = weixinPayHelper( new HttpClient(weiXinProperties.getUnifiedOrderUrl()), map);

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

        Map<String, String> resultMap = weixinPayHelper(new HttpClient(weiXinProperties.getOrderQueryUrl()) ,sendMap);


        String returnCode = resultMap.get("return_code");
        if(returnCode == null || !returnCode.equalsIgnoreCase("SUCCESS")){
            throw  new YYGHException(20000 ,"支付中");
        }
        String resultCode = resultMap.get("result_code");
        if(resultCode == null || !resultCode.equalsIgnoreCase("SUCCESS"))
            throw new YYGHException(  20000, "支付中");
        String tradeState = resultMap.get("trade_state");

        if(tradeState == null || !tradeState.equalsIgnoreCase("SUCCESS")){
            throw new YYGHException(20000 , "支付中");
        }

        String s = HttpClientUtils.postParameters("http://localhost:9998//order/updatePayStatus",
                Map.of("hoscode", orderInfo.getHoscode(), "hosRecordId", orderInfo.getHosRecordId()));
        JSONObject jsonObject = JSON.parseObject(s);
        Integer code = jsonObject.getInteger("code");
        if(code != 200 )
            throw new YYGHException(code , jsonObject.getString("message"));

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

    @Override
    public Map<String, String> refunds(OrderInfo orderInfo) throws Exception {
//        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
//                new FileInputStream(weiXinProperties.getCert()));
//        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(new File(weiXinProperties.getCert()), weiXinProperties.getMchId().toCharArray(), weiXinProperties.getMchId().toCharArray()).build();
//        SSLConnectionSocketFactory sslsf =
//                new SSLConnectionSocketFactory(sslContext,
//                        null ,
//                        null,
//                        new DefaultHostnameVerifier());
//        CloseableHttpClient build = WechatPayHttpClientBuilder.create()
//                .withMerchant(weiXinProperties.getMchId(), weiXinProperties.getMchId(), merchantPrivateKey)
//                .setSSLSocketFactory(sslsf).build();
//
//        HttpPost httpPost = new HttpPost(weiXinProperties.getRefundUrl());
//        String
//        httpPost.setEntity();
//
//        build.execute()


//        build.

        Map<String , String  > sendMap = new HashMap<>();
        sendMap.put("appid" , weiXinProperties.getAppid());
        sendMap.put("mch_id" , weiXinProperties.getMchId());
        sendMap.put("nonce_str" , WXPayUtil.generateNonceStr());

        sendMap.put("out_trade_no" , orderInfo.getOutTradeNo());
        sendMap.put("out_refund_no" , "wx-tk" + orderInfo.getOutTradeNo());

        sendMap.put("refund_fee" ,"1");
        sendMap.put("total_fee" ,"1");


        Map<String, String> resultMap = weixinCertPayHelper(new HttpClient(weiXinProperties.getRefundUrl()), sendMap);


        return resultMap;
    }

    @Override
    public boolean queryRefunds(PaymentInfo paymentInfo, String outRefundNo) {



        try {
            if(outRefundNo == null )
                throw new YYGHException(30001 ,"未查到此退单单号");

            Map<String , String > sendMap = new HashMap<>();
            sendMap.put("appid" , weiXinProperties.getAppid());
            sendMap.put("mch_id" , weiXinProperties.getMchId());
            sendMap.put("nonce_str" , WXPayUtil.generateNonceStr());
            sendMap.put("out_refund_no" ,outRefundNo);

            Map<String, String> resultMap = weixinPayHelper(new HttpClient(weiXinProperties.getQueryrRefundUrl()), sendMap);

            String returnCode = resultMap.get("return_code");
            if (Objects.equals(returnCode , "SUCCESS")) {
                paymentInfo.setPaymentStatus(PaymentStatusEnum.REFUND.getStatus());
                paymentInfoService.updateById(paymentInfo);
                RefundInfo refundInfo = refundInfoService.getByOrderId(paymentInfo.getOrderId());
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackTime(new DateTime(resultMap.get("success_time")).toDate());
                refundInfo.setCallbackContent(JSON.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new YYGHException(30001 , "查询退款信息失败");
        }



    }



    Map<String , String > weixinPayHelper(HttpClient httpClient , Map<String , String > map   ) throws Exception {

        String s = WXPayUtil.generateSignedXml(map , weiXinProperties.getPartnerKey());
        httpClient.setXmlParam(s);
        httpClient.setHttps(true);
        httpClient.post();
        String content = httpClient.getContent();
        return WXPayUtil.xmlToMap(content);
    }



    Map<String , String > weixinCertPayHelper(HttpClient httpClient  ,Map<String , String > map   ) throws Exception {

        httpClient.setCert(true , weiXinProperties.getCert());
        httpClient.setCertPassword(weiXinProperties.getMchId());
        return weixinPayHelper(httpClient , map);

    }


    public  String generateSignature(){

        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder("WECHATPAY2-SHA256-RSA2048mchid=")
                    .append(weiXinProperties.getMchId())
                    .append(",nonce_str=").append(WXPayUtil.generateNonceStr())
                    .append(",signature=" ).append(WXPayUtil.generateSignature(null  , weiXinProperties.getPartnerKey()))
                    .append(",timestamp=").append(System.currentTimeMillis())
                    .append(",serial_no=").append(weiXinProperties.getPartnerKey());
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}
