package com.atguigu.yygh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.constans.MqConst;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.HospClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.UserClient;
import com.atguigu.yygh.utils.HttpClientUtils;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.mapper.OrderMapper;
import com.atguigu.yygh.service.OrderService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.Map;
import java.util.Random;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,OrderInfo> implements OrderService {


    @Autowired
    private  UserClient userClient ;
    @Autowired
    private HospClient hospClient ;

    @Autowired
    private RabbitTemplate rabbitTemplate;
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
}
