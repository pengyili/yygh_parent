package com.atguigu.yygh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.constans.MqConst;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.hosp.HospClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.service.RefundInfoService;
import com.atguigu.yygh.service.WeiXinService;
import com.atguigu.yygh.user.UserClient;
import com.atguigu.yygh.utils.HttpClientUtils;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.mapper.OrderMapper;
import com.atguigu.yygh.service.OrderService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;


@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,OrderInfo> implements OrderService {


    @Autowired
    private  UserClient userClient ;
    @Autowired
    private HospClient hospClient ;
    @Autowired
    private WeiXinService weiXinService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RefundInfoService refundInfoService;
    @Autowired
    private PaymentInfoServiceImpl paymentInfoService;


    @Override
    public Long savaOrder(String token, Long patientId, String scheduleId) {

        Patient patient = userClient.getPatient(patientId);
        ScheduleOrderVo schedule = hospClient.getScheduleOrderVoById(scheduleId);
        OrderInfo orderInfo = new OrderInfo();

//        Map <String , Object > sendMap  = new HashMap<>();

//        sendMap.put("hoscode" , schedule.getHoscode());
//        sendMap.put("depcode" , schedule.getDepcode());
//        sendMap.put("hosScheduleId" , schedule.getHosScheduleId());
//        sendMap.put("reserveDate" , schedule.getReserveDate());
//        sendMap.put("reserveTime" , schedule.getReserveTime());
//        sendMap.put("amount" , schedule.getAmount());


        Map<String, String> sendMap = Map.of("hoscode", (String) schedule.getHoscode(), "depcode", (String) schedule.getDepcode(),
                "hosScheduleId", (String) schedule.getHosScheduleId(), "reserveDate", new DateTime(schedule.getReserveDate()).toString("yyyy年MM月dd日"),
                "reserveTime", String.valueOf(schedule.getReserveTime()), "amount", schedule.getAmount().toString());
        try {
            BeanUtils.copyProperties(orderInfo , schedule);
            orderInfo.setScheduleId(scheduleId);
            String post = HttpClientUtils.postParameters("http://localhost:9998/order/submitOrder" , sendMap);
            JSONObject jsonObject = JSONObject.parseObject(post);

            Integer resultCode = jsonObject.getInteger("code");
            if(resultCode ==  null || !(resultCode == 200)){
                throw new YYGHException(20001 , "挂号失败") ;
            }
            Map data = jsonObject.getObject("data", Map.class);
//            orderInfo.setHosRecordId(jsonObject.getString("hosRecordId"));
//            orderInfo.setNumber(jsonObject.getInteger("number"));
//            orderInfo.setFetchTime(jsonObject.getString("fetchTime"));
//            orderInfo.setFetchAddress(jsonObject.getString("fetchAddress"));
            BeanUtils.copyProperties(orderInfo  , data );



            OrderMqVo orderMqVo = new OrderMqVo();

            BeanUtils.copyProperties(orderMqVo , data);

            orderMqVo.setScheduleId(scheduleId);

            String outTradeNo =  System.currentTimeMillis()  + "" + new Random().nextInt(100);



            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_ORDER , MqConst.ROUTING_ORDER , orderMqVo);
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_MSM ,MqConst.QUEUE_MSM_ITEM , orderMqVo);

            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setPatientId(patientId);
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

            save(orderInfo);
            return orderInfo.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new YYGHException(20001 , "挂号失败");
        }
    }
    public void setOrderStatus(OrderInfo orderInfo){
        orderInfo.setParam(Map.of("orderStatusString" , OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus())));
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId)  {
        OrderInfo orderInfo = getById(orderId);
        PaymentInfo paymentInfo = paymentInfoService.getOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, orderId));
        if(orderInfo == null)
            throw new YYGHException( 21100, "不存在此订单");

        try {
            String s = HttpClientUtils.postParameters("http://localhost:9998/order/updateCancelStatus", Map.of("hoscode", orderInfo.getHoscode(), "hosRecordId", orderInfo.getHosRecordId()));
            JSONObject jsonObject = JSON.parseObject(s);
            String code = jsonObject.getString("code");
            if(!Objects.equals(code , "200"))
                throw new YYGHException(200 ,  jsonObject.getString("message"));

            if(!Objects.equals(OrderStatusEnum.PAID.getStatus(), orderInfo.getOrderStatus())){
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                updateById(orderInfo);
                OrderMqVo orderMqVo = new OrderMqVo();
                orderMqVo.setScheduleId(orderInfo.getScheduleId());
                rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_ORDER , MqConst.QUEUE_ORDER , orderMqVo );
                return;
            }
            Map<String , String > refundMap =  weiXinService.refunds(orderInfo);

            //微信退款申请成功 修改订单状态

            //设置定时器 定时查询退款状态
            Timer timer = new Timer(false);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log.debug("定时器开始执行,查询订单状态");
                    boolean success = false;
                        success = weiXinService.queryRefunds(paymentInfo , refundMap.get("out_refund_no"));
                        log.debug("退款结果{}" , success ? "退款成功" :"处理中");
                    if(success) {
                        timer.cancel();
                        log.debug("订单状态修改完成,清楚定时器");
                    }

                }
            },  60 * 1000, 60 *1000);

            //保存退款信息
            if(refundInfoService.getOne(new LambdaQueryWrapper<RefundInfo>().eq(RefundInfo::getOrderId , orderId)) == null ) {
                RefundInfo refundInfo = new RefundInfo();
                refundInfo.setOrderId(orderId);
                refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
                refundInfo.setSubject(paymentInfo.getSubject());
                refundInfo.setTradeNo(refundMap.get("refund_id"));
                refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
                refundInfo.setOutTradeNo(refundMap.get("out_refund_no"));
                refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
                refundInfoService.save(refundInfo);
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                this.updateById(orderInfo);
            }
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_ORDER , MqConst.QUEUE_ORDER , orderMqVo );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }




}
